package com.qr_restaurant.table.web.dtos.responses.common;

public record TableDto(
        String id,
        int capacity,
        String status
) {
}
