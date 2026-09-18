package com.qr_restaurant.order.repository;

import com.qr_restaurant.order.entities.Order;

/**
 * Stores orders. Saving also publishes the order's recorded events, which is how the
 * kitchen screens hear about new orders and status changes.
 */
public interface OrderRepository {

    /**
     * Inserts a new order or saves changes to an existing one, with its lines.
     *
     * @return the stored order
     */
    Order save(Order order);
}
