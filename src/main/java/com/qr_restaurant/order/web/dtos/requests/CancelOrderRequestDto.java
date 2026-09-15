package com.qr_restaurant.order.web.dtos.requests;

public record CancelOrderRequestDto(String cancelledBy, String reason) {
}
