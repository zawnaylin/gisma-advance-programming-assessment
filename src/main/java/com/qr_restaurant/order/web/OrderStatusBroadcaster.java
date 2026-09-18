package com.qr_restaurant.order.web;

import com.qr_restaurant.common.UiBroadcaster;
import com.qr_restaurant.order.events.OrderStatusChanged;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Pushes order changes to open kitchen views, including cancellations made by the
 * DiningSessionEnded listener on its async thread.
 */
@Component
public class OrderStatusBroadcaster extends UiBroadcaster<OrderStatusChanged> {

    // After commit, so views that re-read the orders see the change.
    @TransactionalEventListener(fallbackExecution = true)
    void on(OrderStatusChanged event) {
        broadcast(event);
    }
}
