##System2-example

This demonstrates the use of the Sunbird-actor library to run a remote actor system.

The actors are instantiated, but never triggered unless from outside. See system1-example
for hitting this actor system (system2-example).

Note that the actor /HelloGreeter in the configuration file (system2.conf) is instantiated
to have 2 instances. Compare this with /HelloGreeter in the system1.conf file, which was

    /HelloGreeter {
            # In System1 example, the helloGreeter is local. If you want this to be remote
            # uncomment the line below and set system name, IP and port as appropriate
            remote = "akka.tcp://MySecondActorSystem@127.0.0.1:9089/user/HelloGreeter"
    }