package com.qr_restaurant.table.application.events;

import com.qr_restaurant.table.application.vo.DiningSessionId;
import com.qr_restaurant.table.application.vo.TableId;

/**
 * Published when a dining session is ended, by the customer or by staff. The session is locked for new orders
 * and the table is waiting to be cleaned. Other modules react without the table module knowing them.
 *
 * @param sessionId the session that ended
 * @param tableId   the table it was at
 */
public record DiningSessionEnded(DiningSessionId sessionId, TableId tableId) {
}
