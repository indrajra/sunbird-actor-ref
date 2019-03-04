package org.sunbird.akka.core;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.dispatch.OnComplete;
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
    public void onReceive(MessageProtos.Message request) {
        route(request);
    }

    /**
     * Gets the source actor name who sent this request
     * If request doesn't contain source, then this identifies from context.
     * @param request
     * @return
     */
    public String getSourceActorName(MessageProtos.Message request) {
        String simpleName = request.getSourceActorName();
        if (simpleName == null || simpleName.isEmpty()) {
            if (!sender().path().parent().equals(sender().path().root())) {
                simpleName = sender().path().parent().name();
            }
        }
        return simpleName;
    }

    /**
     * Tells the request to the target - local or remote
     * @param request
     * @return
     */
    public boolean tellToTarget(MessageProtos.Message request) {
        ActorRef ref = ActorCache.instance().get(request.getTargetActorName());
        ActorSelection actorSelection = null;
        if (ref == null) {
            actorSelection = ActorCache.instance().getRemote(request.getTargetActorName());
        }

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
        return (ref != null || actorSelection != null);
    }

    /**
     * Routes the message to the relevant target
     * Also sets the source before routing it
     * @param request
     */
    public void route(MessageProtos.Message request) {
        // set source
        String simpleSource = getSourceActorName(request);
        MessageProtos.Message msgToSend = MessageProtos.Message.newBuilder(request).setSourceActorName(simpleSource).build();

        String targetActorName = request.getTargetActorName();
        if (msgToSend.getSourceActorName() != null && request.getTargetActorName() != null &&
                targetActorName.equals(msgToSend.getSourceActorName())) {
            logger.error("Eh! sending messages to self. Recheck logic");
            return;
        }

        if (!tellToTarget(msgToSend)) {
            onResponse(msgToSend, new Exception("Actor not found"));
        }
    }

    /**
     * Asks the actor to reply to the message.
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
//                        if (failure instanceof AskTimeoutException) {
//                            logger.info(sender().path().name() + "AskTimeoutException");
//                        } else
                        if (failure != null) {
                            // We got a failure, handle it here
                            logger.error(failure.getMessage(), failure);
                            failure.printStackTrace();
                        }
                        onResponse(message, failure);
                    }
                },
                ec);
        return true;
    }
}

