package org.sunbird.akka.example2;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.sunbird.akka.core.SunbirdActorFactory;

public class Main {
    public static void main(String[] args) {
        Config config = ConfigFactory.parseResources("system2.conf");

        SunbirdActorFactory sunbirdActorFactory = new SunbirdActorFactory(config, "org.sunbird.akka.example2");
        sunbirdActorFactory.init("MySecondActorSystem");
    }
}
