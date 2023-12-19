package mate.academy.listeners;

import mate.academy.Event;
import mate.academy.EventListener;

public class AbstractEventListener implements EventListener {
    private volatile Event processedEvent = null;

    @Override
    public void onEvent(Event e) {
        this.processedEvent = e;
    }

    @Override
    public Event getProcessedEvent() {
        return processedEvent;
    }
}
