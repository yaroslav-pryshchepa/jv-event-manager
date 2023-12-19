package mate.academy;

import mate.academy.listeners.LoggingListener;
import mate.academy.listeners.SampleListener;

public class Main {
    // Feel free to play with this main method during the task implementation
    public static void main(String[] args) {
        EventManager manager = new EventManager();
        EventListener sampleListener = new SampleListener();
        EventListener loggingListener = new LoggingListener();

        manager.registerListener(sampleListener);
        manager.registerListener(loggingListener);

        // Simulating an event
        Event event = new Event("UserLogin", "User123");
        manager.notifyEvent(event);

        // Deregistering a listener and firing another event
        manager.deregisterListener(loggingListener);
        Event anotherEvent = new Event("UserLogout", "User123");
        manager.notifyEvent(anotherEvent);

        manager.shutdown();
    }
}
