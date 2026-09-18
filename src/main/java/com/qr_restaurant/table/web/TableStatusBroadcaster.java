package com.qr_restaurant.table.web;

import com.qr_restaurant.common.UiBroadcaster;
import com.qr_restaurant.table.application.events.TableStatusChanged;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Pushes table status changes to open table views.
 */
@Component
public class TableStatusBroadcaster extends UiBroadcaster<TableStatusChanged> {

    // After commit, so views that re-read the tables see the new status.
    @TransactionalEventListener(fallbackExecution = true)
    void on(TableStatusChanged event) {
        broadcast(event);
    }
}
