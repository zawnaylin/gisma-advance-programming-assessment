package com.qr_restaurant.order.use_cases.commands;

import com.qr_restaurant.order.vo.OrderId;

/**
 * The kitchen has finished cooking an order: CONFIRMED to READY.
 */
public interface MarkOrderReadyCommand {
    /**
     * @throws java.util.NoSuchElementException if there is no order with that id
     * @throws IllegalStateException           if the order's current status forbids it
     */
    void execute(OrderId id);
}
