package com.qr_restaurant.table.application.use_cases.commands;

import com.qr_restaurant.table.application.entities.DiningSession;
import com.qr_restaurant.table.application.vo.DiningSessionId;
import com.qr_restaurant.table.application.vo.TableId;

/**
 * Opens the menu from a scanned QR code.
 */
public interface ScanQrCommand {
    /**
     * Checks that a scanned code still belongs to the table's current session, so a code
     * printed for earlier guests cannot be used again.
     *
     * @param tableId   the table the code was printed for
     * @param sessionId the session the code encodes
     * @return the session the customer may order in
     * @throws IllegalStateException if the table has no session accepting orders, or the code
     *                               belongs to an older session
     */
    DiningSession execute(TableId tableId, DiningSessionId sessionId);
}
