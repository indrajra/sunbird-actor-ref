package org.sunbird.akka.config;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.dsl.Creators;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.sunbird.akka.config.ActorConfiguration;

/**
 * Defines the configuration format expected by this library
 */
public class ConfigProcessor {
    private Config config;

    public ConfigProcessor(Config config) {
        this.config = config;
    }

    public Config getConfig() {
        return config;
    }
}
