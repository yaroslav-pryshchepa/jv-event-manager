package mate.academy.listeners;

import mate.academy.Event;

public class LoggingListener extends AbstractEventListener {
    @Override
    public void onEvent(Event e) {
        System.out.println("[LOG] Event: " + e.type());
        super.onEvent(e);
    }
}
