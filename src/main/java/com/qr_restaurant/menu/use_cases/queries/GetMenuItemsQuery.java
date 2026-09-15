package com.qr_restaurant.menu.use_cases.queries;

import com.qr_restaurant.menu.use_cases.queries.dtos.MenuItemView;
import com.qr_restaurant.menu.vo.CategoryId;
import com.qr_restaurant.menu.vo.MenuCatalogueId;

import java.util.List;

public interface GetMenuItemsQuery {
    List<MenuItemView> query(MenuCatalogueId catalogueId, CategoryId categoryId);
}
