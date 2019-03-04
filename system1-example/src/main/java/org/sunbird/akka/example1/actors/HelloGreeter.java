package org.sunbird.akka.example1.actors;

import org.sunbird.akka.core.BaseActor;
import org.sunbird.akka.core.MessageProtos;

// UnComment this annotation if you want the greeter to be local.
// @SunbirdActor
public class HelloGreeter extends BaseActor {
    @Override
    public void onReceive(MessageProtos.Message request) throws Throwable {
        if (request.getPerformOperation().equals("greet")) {
            System.out.println("System1: Hello " + request.getPayload());
        } else {
            System.out.println("I received a message {}. Who are you?" + request.getPayload());
        }
    }
}
