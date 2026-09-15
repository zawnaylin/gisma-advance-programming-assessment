package com.qr_restaurant.order.web.dtos.responses.common;

public record OrderItemDto(String id, String menuItemId, int quantity, double price) {
}
