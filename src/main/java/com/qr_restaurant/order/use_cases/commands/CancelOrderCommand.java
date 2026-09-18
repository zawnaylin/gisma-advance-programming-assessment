package com.qr_restaurant.order.use_cases.commands;

import com.qr_restaurant.order.use_cases.commands.dtos.CancelOrderDto;
import com.qr_restaurant.order.vo.OrderId;

/**
 * Cancels an order that the kitchen hasn't finished yet: PENDING or CONFIRMED to CANCELLED.
 * A READY order can no longer be cancelled, only served.
 */
public interface CancelOrderCommand {
    /**
     * @param id      the order to cancel
     * @param request who cancelled it and why, kept on the order for the record
     * @throws java.util.NoSuchElementException if there is no order with that id
     * @throws IllegalStateException           if the order is already served or cancelled
     */
    void execute(OrderId id, CancelOrderDto request);
}
