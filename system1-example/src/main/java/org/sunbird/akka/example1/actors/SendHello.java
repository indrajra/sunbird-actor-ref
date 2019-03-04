package org.sunbird.akka.example1.actors;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.protobuf.Value;
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
                .setPerformOperation("greet");
        Value.Builder payloadBuilder = pMsgBuilder.getPayloadBuilder();
        ObjectNode person = JsonNodeFactory.instance.objectNode();
        person.put("name", "Ram");
        person.put("age", 16);

        payloadBuilder.setStringValue(person.toString());
        pMsgBuilder.setPayload(payloadBuilder);

        if (request.getPerformOperation() != null && request.getPerformOperation().equals("sendToBadGreeter")) {
            pMsgBuilder.setTargetActorName("BadGreeter");
            pMsgBuilder.setMsgOption(MessageProtos.MessageOption.GET_BACK_RESPONSE);
        } else if (request.getPerformOperation().equals("selfMessage")) {
            pMsgBuilder.setTargetActorName("SendHello");
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
