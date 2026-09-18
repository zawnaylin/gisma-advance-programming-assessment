package com.qr_restaurant.common;

import com.vaadin.flow.shared.Registration;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Fans events out to every open view that registered for them. Subclasses decide which events to
 * forward and when (typically a {@code @TransactionalEventListener} that calls {@link #broadcast}).
 * In-memory, so it only reaches browsers connected to this application instance.
 *
 * @param <E> the event type the views are interested in
 */
@Slf4j
public abstract class UiBroadcaster<E> {

    // Views register and unregister from many request threads while events are being delivered.
    private final List<Consumer<E>> listeners = new CopyOnWriteArrayList<>();

    /**
     * Subscribes a view to these events, normally when it is attached.
     *
     * @param listener called for each event, on the thread that caused it
     * @return a handle the view removes when it is detached
     */
    public Registration register(Consumer<E> listener) {
        listeners.add(listener);
        return () -> listeners.remove(listener);
    }

    /**
     * Hands the event to every registered view. A view that fails is logged and skipped, so
     * one closed browser tab cannot fail the request that caused the event.
     */
    protected void broadcast(E event) {
        for (var listener : listeners) {
            try {
                listener.accept(event);
            } catch (RuntimeException e) {
                // e.g. a browser tab closed mid-delivery; it must not fail the request that caused the event.
                log.debug("Could not notify a view about {}", event, e);
            }
        }
    }
}
