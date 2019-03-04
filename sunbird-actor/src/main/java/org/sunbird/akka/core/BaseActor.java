package org.sunbird.akka.core;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * An abstract actor
 */
public abstract class BaseActor extends UntypedAbstractActor {
    protected final LoggingAdapter logger = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public void preStart() throws Exception {
        super.preStart();
        String name = self().path().name();
        logger.info("Actor {} getting ready", name);
    }

    public abstract void onReceive(MessageProtos.Message request) throws Throwable;

    @Override
    public void onReceive(Object message) throws Throwable {
        MessageProtos.Message incomingMsg = null;
        String actorName = "";
        try {
            incomingMsg = (MessageProtos.Message) message;
            String operation = incomingMsg.getPerformOperation();
            if (operation != null && operation.equals("handleFailure")) {
                handleFailure(incomingMsg);
            } else {
                onReceive(incomingMsg);
            }
        } catch (ClassCastException e) {
            logger.info("Ignoring message because it is not in expected format {}", message.toString());
        }

    }

    protected void onReceiveException(MessageProtos.Message message, Throwable exception) {
        logger.info("Exception in message processing for {} :: message: {}", message.getSourceActorName(), exception.getMessage(), exception);
        Message errMsg = new Message(message.getSourceActorName(),
                message.getTargetActorName());
        errMsg.setPerformOperation("handleFailure");
        errMsg.setPayload(exception);

        // Tell the source about the exception
        ActorRef localActor = ActorCache.instance().get(errMsg.getSourceActorName());
        if (localActor == null) {
            ActorSelection actorSelection = ActorCache.instance().getRemote(errMsg.getSourceActorName());
            if (actorSelection != null) {
                actorSelection.tell(errMsg, self());
            }
        } else {
            localActor.tell(errMsg, self());
        }
    }

    public void handleFailure(MessageProtos.Message message) {
        logger.error("Generic failure handler {}", message.toString());
    }

    public String getAbsoluteName() {
        return ActorUtils.getName(self());
    }
}

