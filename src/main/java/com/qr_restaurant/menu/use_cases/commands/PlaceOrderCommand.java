package com.qr_restaurant.menu.use_cases.commands;

import com.qr_restaurant.menu.use_cases.commands.dtos.PlaceOrderDto;

public interface PlaceOrderCommand {
    void execute(PlaceOrderDto request);
}
