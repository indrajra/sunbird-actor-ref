package org.sunbird.akka.core;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.FromConfig;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueType;
import org.reflections.Reflections;
import org.sunbird.akka.config.ConfigProcessor;
import org.sunbird.akka.config.SunbirdActor;

import java.util.Set;

/**
 * This class will be responsible for bringing up the actor system and actors.
 */
public class SunbirdActorFactory {
    private ConfigProcessor configProcessor;
    private String actorScanPackage;
    private ActorSystem actorSystem;
    private ActorRef router;

    public SunbirdActorFactory(Config config, String actorScanPackage) {
        this.configProcessor = new ConfigProcessor(config);
        this.actorScanPackage = actorScanPackage;
    }

    /**
     * Inits the basic needs of the utility library
     * @param name
     */
    public void init(String name) {
        createActorSystem(name);
        createRouter();
        initActors(getActors());
        printCache();
    }

    /**
     * Creates the actor system
     * @param name
     */
    private void createActorSystem(String name) {
        Config config = configProcessor.getConfig();
        actorSystem = ActorSystem.create(name, config.getConfig(name));
    }

    /**
     * Creates a router
     */
    private void createRouter() {
        router = actorSystem.actorOf(
                FromConfig.getInstance()
                        .props(
                                Props.create(Router.class).withDispatcher(getDispatcherName(Router.class))),
                Router.class.getSimpleName());
        ActorCache.instance().add(Router.ROUTER_NAME, router);
    }

    /**
     * Gets actors that are annotated "@SunbirdActor"
     * @return
     */
    private Set<Class<? extends BaseActor>> getActors() {
        synchronized (Router.class) {
            Reflections reflections = new Reflections(actorScanPackage);
            Set<Class<? extends BaseActor>> actors = reflections.getSubTypesOf(BaseActor.class);
            return actors;
        }
    }

    /**
     * Init actors both local and remote
     * @param actors
     */
    private void initActors(Set<Class<? extends BaseActor>> actors) {
        ActorCache actorCache = ActorCache.instance();

        // Local actors initialization done.
        for (Class<? extends BaseActor> actor : actors) {
            SunbirdActor routerDetails = actor.getAnnotation(SunbirdActor.class);
            if (null != routerDetails) {
                ActorRef actorRef = createActor(actorSystem, actor);
                actorCache.add(ActorUtils.getName(actorRef), actorRef);
            }
        }

        // Remote actors
        ConfigObject deployed = configProcessor.getConfig().getObject(this.actorSystem.name() + ".akka.actor.deployment");
        deployed.entrySet().forEach(stringConfigValueEntry -> {
            ConfigValue val = stringConfigValueEntry.getValue();
            if (val.valueType().compareTo(ConfigValueType.OBJECT) == 0) {
                ConfigObject valObj = (ConfigObject) val;
                if (valObj.containsKey("remote")) {
                    String remotePath = valObj.get("remote").render().replace("\"","");
                    ActorSelection selection =
                            this.actorSystem.actorSelection(remotePath);
                    actorCache.add(stringConfigValueEntry.getKey().substring(1), selection);
                }
            }
        });
    }

    private String getDispatcherName(Class<? extends BaseActor> actor) {
        String completePath = this.actorSystem.name() + ".akka.actor.deployment./" + actor.getSimpleName()+ ".dispatcher";
        String dispatcher = "";
        try {
            configProcessor.getConfig().getString(completePath);
        } catch (ConfigException missingConfig) {
            // System.out.println("Dispatcher not provided and so default");
        }
        return dispatcher;
    }

    /**
     * Creates an actor
     * @param actorContext
     * @param actor
     * @return
     */
    private ActorRef createActor(
            ActorSystem actorContext,
            Class<? extends BaseActor> actor) {
        Props props;
        String dispatcher = getDispatcherName(actor);

        if (null != dispatcher) {
            props = Props.create(actor).withDispatcher(dispatcher);
        } else {
            props = Props.create(actor);
        }

        ActorRef actorRef = actorContext.actorOf(FromConfig.getInstance().props(props), actor.getSimpleName());
        return actorRef;
    }

    private void printCache() {
        ActorCache.instance().print();
    }
}
