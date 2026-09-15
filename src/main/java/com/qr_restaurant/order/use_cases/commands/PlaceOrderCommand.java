package com.qr_restaurant.order.use_cases.commands;

import com.qr_restaurant.order.use_cases.commands.dtos.PlaceOrderDto;
import com.qr_restaurant.order.vo.OrderId;

public interface PlaceOrderCommand {
    OrderId execute(PlaceOrderDto request);
}
