package com.qr_restaurant.table.application.use_cases.commands;

import com.qr_restaurant.table.application.vo.DiningSessionId;
import com.qr_restaurant.table.application.vo.TableId;

/**
 * Ends a dining session: it stops accepting orders and the table waits to be cleaned.
 * Publishes DiningSessionEnded, which the order module uses to cancel unfinished orders.
 */
public interface EndDiningSessionCommand {
    /**
     * Customer side: ends the session they are ordering in.
     *
     * @param id the session to end
     * @throws java.util.NoSuchElementException if there is no session with that id
     * @throws IllegalStateException            if that session has already ended
     */
    void execute(DiningSessionId id);

    /**
     * Staff side: ends whatever session is currently running at the table.
     *
     * @param tableId the table whose session to end
     * @throws java.util.NoSuchElementException if there is no table with that id
     * @throws IllegalStateException            if nobody is seated at the table
     */
    void execute(TableId tableId);
}
