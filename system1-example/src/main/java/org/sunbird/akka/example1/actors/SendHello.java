package org.sunbird.akka.example1.actors;

import org.sunbird.akka.config.SunbirdActor;
import org.sunbird.akka.core.ActorCache;
import org.sunbird.akka.core.BaseActor;
import org.sunbird.akka.core.MessageProtos;
import org.sunbird.akka.core.Router;

import java.util.UUID;


@SunbirdActor
public class SendHello extends BaseActor {
    @Override
    public void onReceive(MessageProtos.Message request) throws Throwable {
        MessageProtos.Message.Builder pMsgBuilder = MessageProtos.Message.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setPayload("Ram")
                .setPerformOperation("greet");

        if (request.getPerformOperation() != null && request.getPerformOperation().equals("sendToBadGreeter")) {
            pMsgBuilder.setTargetActorName("BadGreeter");
        } else {
            pMsgBuilder.setTargetActorName("HelloGreeter");
        }

        ActorCache.instance().get(Router.ROUTER_NAME).tell(pMsgBuilder.build(), self());
    }

    @Override
    public void handleFailure(MessageProtos.Message message) {
        logger.info("Send hello failed {}", message.toString());
    }
}
