package org.sunbird.akka.core;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Address;
import akka.actor.dsl.Creators;

import java.util.HashMap;
import java.util.Map;

/**
 * Caches the operation and its corresponding ActorRef
 */
public class ActorCache {
    private final static Map<String, ActorRef> routingMap = new HashMap<>();
    private final static Map<String, ActorSelection> remoteRoutingMap = new HashMap<>();
    private static ActorCache cacheRef = null;
    private static Boolean localLock = false;

    private ActorCache() {}
    public static ActorCache instance() {
        if (!localLock) {
            synchronized (localLock) {
                localLock = Boolean.TRUE;
                if (null == cacheRef) {
                    cacheRef = new ActorCache();
                }
            }
        }
        return cacheRef;
    }

    public void add(String absName, ActorRef actor) {
        routingMap.put(absName, actor);
    }

    public void add(String absName, ActorSelection actor) {
        remoteRoutingMap.put(absName, actor);
    }

    public ActorRef get(String simpleName) {
        return routingMap.get(simpleName);
    }

    public ActorSelection getRemote(String simpleName) {
        return remoteRoutingMap.get(simpleName);
    }

    protected void print() {
        routingMap.forEach((k,v) -> {
            System.out.println(k + " -> " + v);
        });
    }
}