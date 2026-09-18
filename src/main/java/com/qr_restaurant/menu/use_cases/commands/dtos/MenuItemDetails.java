package com.qr_restaurant.menu.use_cases.commands.dtos;

import com.qr_restaurant.menu.vo.CategoryId;
import com.qr_restaurant.menu.vo.MenuCatalogueId;

public record MenuItemDetails(
        String name,
        String description,
        double price,
        MenuCatalogueId catalogueId,
        CategoryId categoryId
) {
}
