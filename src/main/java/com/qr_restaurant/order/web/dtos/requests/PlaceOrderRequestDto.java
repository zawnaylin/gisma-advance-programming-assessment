package com.qr_restaurant.order.web.dtos.requests;

import java.util.List;

public record PlaceOrderRequestDto(String diningSessionId, List<Item> items) {
    public record Item(String menuItemId, int quantity) {
    }
}
