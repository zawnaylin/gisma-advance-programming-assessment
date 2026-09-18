package com.qr_restaurant.order.use_cases.queries;

import com.qr_restaurant.order.entities.Order;
import com.qr_restaurant.table.application.vo.DiningSessionId;

import java.util.List;

/**
 * Lists every order of one dining session, whatever its status.
 */
public interface ViewSessionOrdersQuery {
    /**
     * @param diningSessionId the session to list orders for
     * @return the session's orders, in no particular order
     */
    List<Order> query(DiningSessionId diningSessionId);
}
