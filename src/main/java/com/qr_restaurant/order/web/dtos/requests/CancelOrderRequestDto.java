package com.qr_restaurant.order.web.dtos.requests;

import jakarta.validation.constraints.NotBlank;

public record CancelOrderRequestDto(
        @NotBlank String cancelledBy,
        String reason
) {
}
