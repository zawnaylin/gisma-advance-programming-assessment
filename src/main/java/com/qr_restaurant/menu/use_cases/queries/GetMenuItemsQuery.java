package com.qr_restaurant.menu.use_cases.queries;

import com.qr_restaurant.menu.vo.MenuCatalogueId;
import com.qr_restaurant.menu.vo.CategoryId;

import java.awt.*;
import java.util.List;

public interface GetMenuItemsQuery {
    List<MenuItem> query(MenuCatalogueId catalogueId, CategoryId categoryId);
}
