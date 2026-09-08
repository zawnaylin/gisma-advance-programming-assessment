package com.qr_restaurant.table.web.dtos.responses;

public record GetQRResponseDto(
        String url,
        String tableId,
        String diningSessionId
) {
}
