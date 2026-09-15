package com.qr_restaurant.menu.web.dtos.responses.common;

public record MenuItemDto(
        String id,
        String name,
        String description,
        double price,
        String catalogueId,
        String categoryId
) {
}
