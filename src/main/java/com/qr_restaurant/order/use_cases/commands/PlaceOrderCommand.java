package com.qr_restaurant.order.use_cases.commands;

import com.qr_restaurant.order.use_cases.commands.dtos.PlaceOrderDto;
import com.qr_restaurant.order.vo.OrderId;

/**
 * Places a customer's order in their dining session. The price of each line is copied from
 * the menu at this moment, so later menu changes leave placed orders alone.
 */
public interface PlaceOrderCommand {
    /**
     * @param request the dining session and the items with their quantities
     * @return the id of the new order, which starts as PENDING
     * @throws IllegalArgumentException if one of the menu items doesn't exist
     * @throws IllegalStateException    if the dining session is no longer accepting orders
     */
    OrderId execute(PlaceOrderDto request);
}
