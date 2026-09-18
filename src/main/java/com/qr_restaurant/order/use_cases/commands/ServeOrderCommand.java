package com.qr_restaurant.order.use_cases.commands;

import com.qr_restaurant.order.vo.OrderId;

/**
 * The order has been brought to the table: READY to SERVED, which is final.
 */
public interface ServeOrderCommand {
    /**
     * @throws java.util.NoSuchElementException if there is no order with that id
     * @throws IllegalStateException           if the order's current status forbids it
     */
    void execute(OrderId id);
}
