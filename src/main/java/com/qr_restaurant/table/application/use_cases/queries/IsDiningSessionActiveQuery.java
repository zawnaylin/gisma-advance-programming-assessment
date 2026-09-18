package com.qr_restaurant.table.application.use_cases.queries;

import com.qr_restaurant.table.application.vo.DiningSessionId;

/**
 * Whether a dining session still accepts orders. The order module asks this before
 * placing an order, rather than reaching into the table module's data.
 */
public interface IsDiningSessionActiveQuery {
    /**
     * @param id the session to check
     * @return {@code true} while guests may order, {@code false} once the session has ended
     *         or if there is no session with that id
     */
    boolean query(DiningSessionId id);
}
