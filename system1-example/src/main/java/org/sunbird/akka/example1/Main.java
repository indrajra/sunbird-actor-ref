package org.sunbird.akka.example1;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.sunbird.akka.core.ActorCache;
import org.sunbird.akka.core.MessageProtos;
import org.sunbird.akka.core.Router;
import org.sunbird.akka.core.SunbirdActorFactory;

public class Main {
    public static void main(String[] args) {
        Config config = ConfigFactory.parseResources("system1.conf");

        SunbirdActorFactory sunbirdActorFactory = new SunbirdActorFactory(config, "org.sunbird.akka.example1.actors");
        sunbirdActorFactory.init("MyFirstActorSystem");

        // Case 1 - Sending to an invalid actor
        MessageProtos.Message.Builder toInvalidActorProtos = MessageProtos.Message.newBuilder()
                .setTargetActorName("S1endHello");
        MessageProtos.Message toInvalidActor = toInvalidActorProtos.build();
        ActorCache.instance().get(Router.ROUTER_NAME).tell(toInvalidActor,null);

        // Case 2 - Sending to an invalid actor and expecting response
        toInvalidActorProtos.setMsgOption(MessageProtos.MessageOption.GET_BACK_RESPONSE);
        MessageProtos.Message toInvalidActorExpectResponseMsg = toInvalidActorProtos.build();
        ActorCache.instance().get(Router.ROUTER_NAME).tell(toInvalidActorExpectResponseMsg,null);

        // Case 3 - Sending to a valid actor
        MessageProtos.Message sendHelloMessage = MessageProtos.Message.newBuilder()
                                                .setTargetActorName("SendHello")
                                                .build();
        ActorCache.instance().get(Router.ROUTER_NAME).tell(sendHelloMessage,null);

        // Case 4 - Sending to a valid but bad actor
        MessageProtos.Message toBadActor = MessageProtos.Message.newBuilder()
                .setTargetActorName("SendHello")
                .setPerformOperation("sendToBadGreeter")
                .build();
        ActorCache.instance().get(Router.ROUTER_NAME).tell(toBadActor,null);

        // Case 5 - Sending message to itself inadvertently - no looping
        MessageProtos.Message selfMessage = MessageProtos.Message.newBuilder()
                .setTargetActorName("SendHello")
                .setPerformOperation("selfMessage")
                .build();
        ActorCache.instance().get(Router.ROUTER_NAME).tell(selfMessage,null);
    }
}
