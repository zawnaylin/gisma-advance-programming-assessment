package com.qr_restaurant.order.use_cases.commands.dtos;

import com.qr_restaurant.menu.vo.MenuItemId;
import com.qr_restaurant.table.application.vo.DiningSessionId;

import java.util.List;

public record PlaceOrderDto(
        DiningSessionId diningSessionId,
        List<Item> items
) {
    public record Item(MenuItemId menuItemId, int quantity) {
    }
}
