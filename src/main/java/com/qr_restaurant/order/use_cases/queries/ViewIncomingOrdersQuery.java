package com.qr_restaurant.order.use_cases.queries;

import com.qr_restaurant.order.entities.Order;
import com.qr_restaurant.order.enums.OrderStatus;

import java.util.List;

/**
 * Lists orders for the kitchen, oldest first, so they are worked in the order they arrived.
 */
public interface ViewIncomingOrdersQuery {
    /**
     * @param statusFilter only orders in this status, or {@code null} for all of them
     * @return the matching orders, oldest first
     */
    List<Order> query(OrderStatus statusFilter);
}
