package com.qr_restaurant.order.use_cases.commands.dtos;

public record CancelOrderDto(String cancelledBy, String reason) {
}
