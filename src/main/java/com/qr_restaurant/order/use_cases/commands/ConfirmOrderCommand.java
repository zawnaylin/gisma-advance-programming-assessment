package com.qr_restaurant.order.use_cases.commands;

import com.qr_restaurant.order.vo.OrderId;

public interface ConfirmOrderCommand {
    void execute(OrderId id);
}
