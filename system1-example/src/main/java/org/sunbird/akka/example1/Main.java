package org.sunbird.akka.example1;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.sunbird.akka.core.ActorCache;
import org.sunbird.akka.core.Message;
import org.sunbird.akka.core.MessageProtos;
import org.sunbird.akka.core.Router;
import org.sunbird.akka.core.SunbirdActorFactory;

public class Main {
    public static void main(String[] args) {
        Config config = ConfigFactory.parseResources("system1.conf");

        SunbirdActorFactory sunbirdActorFactory = new SunbirdActorFactory(config, "org.sunbird.akka.example1");
        sunbirdActorFactory.init("MyFirstActorSystem");

//        // Case 1 - Sending to an invalid actor
//        Message toInvalidActor = new Message("Send1Hello");
//        ActorCache.instance().get(Router.ROUTER_NAME).tell( toInvalidActor,null);
//
//        // Case 2 - Sending to an invalid actor and expecting response
//        toInvalidActor.setMsgOption(Message.MessageOption.GET_BACK_RESPONSE);
//        ActorCache.instance().get(Router.ROUTER_NAME).tell( toInvalidActor,null);

        // Case 2 - Sending to a valid actor
        MessageProtos.Message sendHelloMessage = MessageProtos.Message.newBuilder()
                                                .setTargetActorName("SendHello").build();

        ActorCache.instance().get(Router.ROUTER_NAME).tell( sendHelloMessage,null);

//        // Case 3 - Sending to a valid but bad actor
//        Message toBadActor = new Message("SendHello");
//        toBadActor.setMsgOption(Message.MessageOption.GET_BACK_RESPONSE);
//        toBadActor.setPerformOperation("sendToBadGreeter");
//        ActorCache.instance().get(Router.ROUTER_NAME).tell( toBadActor,null);
    }
}
