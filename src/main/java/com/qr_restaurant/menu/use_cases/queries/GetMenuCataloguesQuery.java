package com.qr_restaurant.menu.use_cases.queries;

import com.qr_restaurant.menu.entities.MenuCatalogue;

import java.util.List;

public interface GetMenuCataloguesQuery {
    List<MenuCatalogue> query();
}
