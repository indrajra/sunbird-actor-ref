package org.sunbird.akka.core;

import akka.actor.ActorRef;

public class ActorUtils {
    public static String getName(ActorRef actor) {
        return actor.path().name();
    }
}
