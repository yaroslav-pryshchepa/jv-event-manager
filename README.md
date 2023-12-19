# Implement a thread-safe asynchronous event manager

Implement a thread-safe `EventManager` class that handles event notifications asynchronously to multiple listeners. The `EventManager` should be able to register listeners, deregister them, and notify them of events in a concurrent environment.

- There is already defined an `EventListener` interface with a single method `onEvent(Event e)`. This method is called whenever an event is notified.
- There is already defined an `Event` class that encapsulates details of an event, such as type and source.

### Requirements

Implement an `EventManager` class with the following capabilities:
- **Listener registration:** Method `registerListener(EventListener listener)` to add a new listener.
- **Listener deregistration:** Method `deregisterListener(EventListener listener)` to remove an existing listener.
- **Event notification:** Method `notifyEvent(Event event)` to asynchronously notify all registered listeners of an event.
- **Shutdown mechanism:** Method `shutdown()` to properly close any resources if required.

P.S. If your solution does not pass tests locally because of `Not all events were processed in time`, try to run them in GitHub. It may be related to your PC configuration (for example not so powerful CPU as expected).
