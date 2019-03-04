package org.sunbird.akka.core;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.dispatch.OnComplete;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

import static org.sunbird.akka.core.MessageProtos.MessageOption.GET_BACK_RESPONSE;
import static org.sunbird.akka.core.MessageProtos.MessageOption.SEND_AND_FORGET;

/**
 * A specialized actor of the library that allows us to direct the
 * Message to the appropriate actor
 */
public class Router extends BaseActor {
    /**
     * Sunbird Actor router
     */
    public static final String ROUTER_NAME = "sbarouter";

    /**
     * Public to override by implementors
     */
    public static int WAIT_TIME_VALUE = 10;

    @Override
    public void onReceive(MessageProtos.Message request) throws Throwable {
        route(request);
    }

//    public void setDestination(Message request) {
//        String simpleName = request.getTargetActorName();
//        String absName = ActorCache.instance().getAbsoluteName(simpleName);
//        request.setTargetActorName(absName);
//    }
//
    public void setSource(MessageProtos.Message request) {
        String simpleName = request.getSourceActorName();
        if (simpleName == null || simpleName.isEmpty()) {
            if (!sender().path().parent().equals(sender().path().root())) {
                simpleName = sender().path().parent().name();
            }
        }

        request = MessageProtos.Message.newBuilder(request).setSourceActorName(simpleName).build();
    }

    public void route(MessageProtos.Message request) {
        // set source
        setSource(request);

        String targetActorName = request.getTargetActorName();
        ActorRef ref = ActorCache.instance().get(targetActorName);

        if (request.getSourceActorName() != null && request.getTargetActorName() != null &&
                targetActorName.equals(request.getSourceActorName())) {
            logger.error("Eh! sending messages to self. Recheck logic");
            return;
        }

        ActorSelection actorSelection = null;
        if (ref == null) {
            actorSelection = ActorCache.instance().getRemote(targetActorName);
        }

        if (null != ref || null != actorSelection) {
            if (request.getMsgOption() ==
                    SEND_AND_FORGET) {
                if (ref != null) {
                    ref.tell(request, self());
                } else {
                    actorSelection.tell(request, self());
                }
            } else if (request.getMsgOption() == GET_BACK_RESPONSE) {
                route(actorSelection, ref, request, getContext().dispatcher());
            }
        } else {
            onReceiveException(request, new Exception("Actor not found"));
        }
    }

    /**
     * method will route the message to corresponding router pass into the argument .
     *
     * @param router
     * @param message
     * @return boolean
     */
    private boolean route(ActorSelection router, ActorRef ref, MessageProtos.Message message, ExecutionContext ec) {
        logger.info("Actor Service Call start for api {}", message.getTargetActorName());
        Timeout timeout = new Timeout(Duration.create(WAIT_TIME_VALUE, TimeUnit.SECONDS));
        Future<Object> future;
        if (router == null) {
            future = Patterns.ask(ref, message, timeout);
        } else {
            future = Patterns.ask(router, message, timeout);
        }
        future.onComplete(
                new OnComplete<Object>() {
                    @Override
                    public void onComplete(Throwable failure, Object result) {
                        if (failure != null) {
                            // We got a failure, handle it here
                            logger.error(failure.getMessage(), failure);
                            failure.printStackTrace();
                            onReceiveException(message, failure);
                        } else {
                            // success, no need to tell anybody anything
                        }
                    }
                },
                ec);
        return true;
    }
}

