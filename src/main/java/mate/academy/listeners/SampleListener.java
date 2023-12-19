package mate.academy.listeners;

import mate.academy.Event;

public class SampleListener extends AbstractEventListener {
    @Override
    public void onEvent(Event e) {
        System.out.println("Received event: " + e.type() + " from " + e.source());
        super.onEvent(e);
    }
}
