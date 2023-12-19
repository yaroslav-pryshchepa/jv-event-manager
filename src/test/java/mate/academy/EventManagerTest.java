package mate.academy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import mate.academy.listeners.SampleListener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class EventManagerTest {
    private EventManager manager;

    @BeforeEach
    void setUp() {
        manager = new EventManager();
    }

    @AfterEach
    void tearDown() {
        manager.shutdown();
    }

    @Test
    @Timeout(value = 2)
    void notifyEvent_SingleEvent_CorrectEventProcessed() throws InterruptedException {
        // given
        SampleListener listener = new SampleListener();
        manager.registerListener(listener);
        Event event = new Event("TestEvent", this);

        // when
        manager.notifyEvent(event);

        // then

        assertEventuallyProcessed(listener, event, 1, TimeUnit.SECONDS);
    }

    @RepeatedTest(20)
    @Timeout(value = 4)
    void notifyEvent_MultipleEvents_AllEventsProcessed() throws InterruptedException {
        // given
        final int numberOfEvents = 2;
        CountDownLatch latch = new CountDownLatch(numberOfEvents);
        Set<Event> processedEvents = Collections.synchronizedSet(new HashSet<>());
        Set<Event> expectedEvents = new HashSet<>();

        EventListener listener = new EventListener() {
            @Override
            public void onEvent(Event e) {
                processedEvents.add(e);
                latch.countDown();
            }

            @Override
            public Event getProcessedEvent() {
                return null; // Not used in this test
            }
        };

        manager.registerListener(listener);

        // when
        for (int i = 0; i < numberOfEvents; i++) {
            Event event = new Event("Event" + i, this);
            expectedEvents.add(event);
            manager.notifyEvent(event);
        }

        // then
        assertTrue(latch.await(2, TimeUnit.SECONDS), "Not all events were processed in time");
        assertEquals(expectedEvents.size(), processedEvents.size());
        assertTrue(processedEvents.containsAll(expectedEvents), "Not all expected events were processed");
    }

    @Test
    @Timeout(value = 2)
    void deregisterListener_EventAfterDeregistration_NotProcessed() throws InterruptedException {
        // given
        SampleListener listener = new SampleListener();
        manager.registerListener(listener);
        Event event = new Event("TestEvent", this);
        manager.notifyEvent(event);
        assertEventuallyProcessed(listener, event, 1, TimeUnit.SECONDS);

        // when
        manager.deregisterListener(listener);
        Event newEvent = new Event("NewEvent", this);
        manager.notifyEvent(newEvent);

        // then
        TimeUnit.MILLISECONDS.sleep(100); // Giving some time for the event (if any) to be processed
        assertNotEquals(newEvent, listener.getProcessedEvent());
    }

    @Test
    @Timeout(value = 6)
    void notifyEvent_HighVolume_AllEventsProcessed() throws InterruptedException {
        // given
        final int numberOfEvents = 400;
        CountDownLatch latch = new CountDownLatch(numberOfEvents);

        EventListener listener = new EventListener() {
            @Override
            public void onEvent(Event e) {
                latch.countDown();
            }

            @Override
            public Event getProcessedEvent() {
                return null; // Not relevant for this test
            }
        };

        manager.registerListener(listener);

        // when
        for (int i = 0; i < numberOfEvents; i++) {
            manager.notifyEvent(new Event("Event" + i, this));
        }

        // then
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Not all events were processed in time");
    }

    private void assertEventuallyProcessed(
            SampleListener listener,
            Event expectedEvent,
            long timeout,
            TimeUnit unit
    ) throws InterruptedException {
        long startTime = System.nanoTime();
        long timeoutNanos = unit.toNanos(timeout);
        boolean processed = false;

        while (System.nanoTime() - startTime < timeoutNanos && !processed) {
            if (expectedEvent.equals(listener.getProcessedEvent())) {
                processed = true;
            } else {
                TimeUnit.MILLISECONDS.sleep(10); // Sleep a small amount of time before checking again
            }
        }

        assertEquals(expectedEvent, listener.getProcessedEvent());
    }
}
