package com.qr_restaurant.table.web.dtos.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

// id is only used when creating (optional, generated if absent); on update the id comes from the URL.
public record TableRequestDto(
        @Size(max = 255) @Pattern(regexp = "[A-Za-z0-9_-]+", message = "may only contain letters, digits, '-' and '_'")
        String id,
        @NotNull @Positive Integer capacity
) {
}
