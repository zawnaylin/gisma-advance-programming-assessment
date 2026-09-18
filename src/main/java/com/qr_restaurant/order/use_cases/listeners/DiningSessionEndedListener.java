package com.qr_restaurant.order.use_cases.listeners;

import com.qr_restaurant.order.entities.Order;
import com.qr_restaurant.order.use_cases.commands.CancelOrderCommand;
import com.qr_restaurant.order.use_cases.commands.dtos.CancelOrderDto;
import com.qr_restaurant.order.use_cases.queries.ViewSessionOrdersQuery;
import com.qr_restaurant.table.application.events.DiningSessionEnded;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Cancels a session's orders that the kitchen can still cancel once the session ends.
 * READY orders cannot be cancelled, so they stay for the waiter to serve.
 */
@Component
class DiningSessionEndedListener {

    private final ViewSessionOrdersQuery viewSessionOrdersQuery;
    private final CancelOrderCommand cancelOrderCommand;

    DiningSessionEndedListener(ViewSessionOrdersQuery viewSessionOrdersQuery, CancelOrderCommand cancelOrderCommand) {
        this.viewSessionOrdersQuery = viewSessionOrdersQuery;
        this.cancelOrderCommand = cancelOrderCommand;
    }

    // Runs asynchronously after the table module's transaction commits, in its own transaction.
    // If it fails, the event publication stays incomplete and is retried on restart.
    @ApplicationModuleListener
    void on(DiningSessionEnded event) {
        viewSessionOrdersQuery.query(event.sessionId()).stream()
                .filter(Order::canCancel)
                .forEach(order -> cancelOrderCommand.execute(order.getId(),
                        new CancelOrderDto("system", "Dining session ended")));
    }
}
