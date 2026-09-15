package com.qr_restaurant.menu.use_cases.queries.dtos;

import com.qr_restaurant.menu.vo.CategoryId;
import com.qr_restaurant.menu.vo.MenuCatalogueId;
import com.qr_restaurant.menu.vo.MenuItemId;

public record MenuItemView(
        MenuItemId id,
        String name,
        String description,
        double price,
        MenuCatalogueId catalogueId,
        CategoryId categoryId
) {
}
