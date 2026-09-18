package com.qr_restaurant.order.web.dtos.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record PlaceOrderRequestDto(
        @NotBlank String diningSessionId,
        @NotEmpty List<@Valid Item> items
) {
    public record Item(
            @NotBlank String menuItemId,
            @Positive int quantity
    ) {
    }
}
