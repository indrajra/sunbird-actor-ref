package org.sunbird.akka.example1.actors;

import org.sunbird.akka.core.BaseActor;
import org.sunbird.akka.core.MessageProtos;


public class BadGreeter extends BaseActor {
    @Override
    public void onReceive(MessageProtos.Message request) throws Throwable {
       throw new Exception("Am a bad guy");
    }
}
