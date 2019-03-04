package org.sunbird.akka.config;

public class ActorConfiguration {
    /**
     * Where the actor system must listen to
     * Example: :8088 - To allow remote computers to connect to this system
     * Example: 0.0.0.0:8088 - To disallow remote computers to connect to this system
     */
    private String listenAddress;

    /**
     * The dispatcher configuration
     */
    private String dispatcherName;

    public String getListenAddress() {
        return listenAddress;
    }

    public void setListenAddress(String listenAddress) {
        this.listenAddress = listenAddress;
    }

    public String getDispatcherName() {
        return dispatcherName;
    }

    public void setDispatcherName(String dispatcherName) {
        this.dispatcherName = dispatcherName;
    }
}
