package com.qr_restaurant.menu.use_cases.queries;

import com.qr_restaurant.menu.use_cases.queries.dtos.MenuItemView;
import com.qr_restaurant.menu.vo.CategoryId;
import com.qr_restaurant.menu.vo.MenuCatalogueId;

import java.util.List;

/**
 * Lists menu items, optionally filtered.
 */
public interface GetMenuItemsQuery {
    /**
     * @param catalogueId only items in this catalogue, or {@code null} for any
     * @param categoryId  only items in this category, or {@code null} for any
     * @return the matching items, in no particular order
     */
    List<MenuItemView> query(MenuCatalogueId catalogueId, CategoryId categoryId);
}
