package com.qr_restaurant.menu.web.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// id is only used when creating (optional, generated if absent); on update the id comes from the URL.
public record MenuCatalogueRequestDto(
        @Size(max = 255) @Pattern(regexp = "[A-Za-z0-9_-]+", message = "may only contain letters, digits, '-' and '_'")
        String id,
        @NotBlank @Size(max = 255) String name,
        @Size(max = 255) String description
) {
}
