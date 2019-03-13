package org.sunbird.akka.example1.actors;

import org.sunbird.akka.core.BaseActor;
import org.sunbird.akka.core.MessageProtos;
import org.sunbird.akka.example1.MyService;

public class HelloGreeter extends BaseActor {
    MyService service = new MyService();

    private static int i = 0;

    @Override
    public void onReceive(MessageProtos.Message request) throws Throwable {
        if (request.getPerformOperation().equals("greet")) {
            System.out.println("System1: Hello " + request.getPayload());
            service.add("greeting " + ++i + "");
        } else {
            System.out.println("I received a message {}. Who are you?" + request.getPayload());
        }
    }

    @Override
    public void onSuccess(MessageProtos.Message response) {
        service.print();
    }
}
