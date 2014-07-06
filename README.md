resteasy-exception-chains
=========================

This is a small library to provide a uniform mechanism for throwing and catching exceptions through a resteasy api.  The intention is to standardise error handling within applications and to make debugging easier by propagating remote errors all the way back to the client.

The central idea is provided by the SerializableException class. This class implements Exception and is designed to be seralizable to and from JSON (you can't do this with a normal exception class).  Users of this library should register the ExceptionHandler class with their resteasy application.   ExceptionHandler is responsible for catching all Throwables from the application, wrapping them in SerializableException and passing this to the client.

One benefit of a standardised serialised exception is that you can automatically chain exception information across remote API calls.   You do this by registering RemoteFailureHandler with your application. For example:

        ResteasyClient c = new ResteasyClientBuilder().build();
        ResteasyWebTarget t = c.target("http://remote.com/api");
        ExampleApi proxy = t.proxy(ExampleApi.class);
        Item item = proxy.fetchItem("item1");
        return item;

When the proxy call is made we cause the remote server to execute code.  If this throws an exception this is serialised into JSON and returned to us.  This causes the proxy object to throw an exception which is captured in turn by RemoteFailureHandler.  RemoteFailureHandler then extracts the original cause and serialises a further exception to return to the client.

Usage
-----

Include this library in your class path and register ExceptionHandler and RemoteFailureHandler with your application.

