package com.qr_restaurant.order.use_cases.commands;

import com.qr_restaurant.order.use_cases.commands.dtos.CancelOrderDto;
import com.qr_restaurant.order.vo.OrderId;

public interface CancelOrderCommand {
    void execute(OrderId id, CancelOrderDto request);
}
