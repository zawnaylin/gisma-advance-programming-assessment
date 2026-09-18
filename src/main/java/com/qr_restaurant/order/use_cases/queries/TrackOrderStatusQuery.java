package com.qr_restaurant.order.use_cases.queries;

import com.qr_restaurant.order.entities.Order;
import com.qr_restaurant.order.vo.OrderId;

/**
 * Looks up one order so a customer can follow its progress.
 */
public interface TrackOrderStatusQuery {
    /**
     * @param id the order to look up
     * @return the order, or {@code null} if there is none with that id
     */
    Order query(OrderId id);
}
