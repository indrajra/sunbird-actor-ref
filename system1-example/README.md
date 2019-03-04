## System1-example

This demonstrates the first basic use of the Sunbird-actor library.

There are several cases presented to demonstrate the following:

*   How to use the router and redirect the message to the appropriate actor

    **Example**:
    ActorCache.instance().get(Router.ROUTER_NAME)
*  How to send a json payload to the message (Refer to SendHello.java - Person)
*  How to ask for the response (Refer to Case 2 in Main())
