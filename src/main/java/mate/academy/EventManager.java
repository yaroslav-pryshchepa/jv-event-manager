package mate.academy;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public class EventManager {
    private final List<EventListener> listenerList = new CopyOnWriteArrayList<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public void registerListener(EventListener listener) {
        Objects.requireNonNull(listener, "Listener must not be null");
        listenerList.add(listener);
    }

    public void deregisterListener(EventListener listener) {
        if (listener != null) {
            listenerList.remove(listener);
        }
    }

    public void notifyEvent(Event event) {
        if (executorService.isShutdown()) {
            System.err.println("EventManager is shut down. Ignoring event: " + event);
            return;
        }

        for (EventListener listener : listenerList) {
            try {
                executorService.submit(() -> {
                    try {
                        listener.onEvent(event);
                    } catch (Exception e) {
                        System.err.println("Listener failed to handle event "
                                + event + ": " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            } catch (RejectedExecutionException e) {
                System.err.println("Rejected execution for event: "
                        + event + " (executor shutting down)");
            }
        }
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(3, TimeUnit.SECONDS)) {
                System.err.println("Forcing shutdown, some tasks may not complete.");
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.err.println("Shutdown interrupted. Forcing shutdown now.");
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}

