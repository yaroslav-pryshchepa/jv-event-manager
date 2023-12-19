package mate.academy;

public interface EventListener {
    void onEvent(Event e);

    Event getProcessedEvent(); // Return the last processed event

}
