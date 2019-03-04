package org.sunbird.akka.example2.actors;

import org.sunbird.akka.config.SunbirdActor;
import org.sunbird.akka.core.BaseActor;
import org.sunbird.akka.core.MessageProtos;

@SunbirdActor
public class HelloGreeter extends BaseActor {
    @Override
    public void onReceive(MessageProtos.Message request) throws Throwable {
        if (request.getPerformOperation().equals("greet")) {
            System.out.println("System2: Hello " + request.getPayload());
            System.out.println("Incoming request -> " + request.toString());
        } else {
            System.out.println("I received a message {}. Who are you?" + request.toString());
        }
    }
}
