package com.qr_restaurant.order.use_cases.commands;

import com.qr_restaurant.order.vo.OrderId;

public interface ServeOrderCommand {
    void execute(OrderId id);
}
