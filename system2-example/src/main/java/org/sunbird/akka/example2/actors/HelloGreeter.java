package org.sunbird.akka.example2.actors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sunbird.akka.config.SunbirdActor;
import org.sunbird.akka.core.BaseActor;
import org.sunbird.akka.core.MessageProtos;
import org.sunbird.akka.example2.Person;

@SunbirdActor
public class HelloGreeter extends BaseActor {
    private ObjectMapper mapper = new ObjectMapper();
    @Override
    public void onReceive(MessageProtos.Message request) throws Throwable {
        if (request.getPerformOperation().equals("greet")) {
            Person person = mapper.readValue(request.getPayload().getStringValue(), Person.class);
            System.out.println("System2: Hello " + person.name + ". You are aged " + person.age);
            System.out.println("Incoming request -> " + request.toString());
        } else {
            System.out.println("I received a message {}. Who are you?" + request.toString());
        }
    }
}
