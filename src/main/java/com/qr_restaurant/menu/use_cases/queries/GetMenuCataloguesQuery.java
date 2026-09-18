package com.qr_restaurant.menu.use_cases.queries;

import com.qr_restaurant.menu.entities.MenuCatalogue;

import java.util.List;

/**
 * Lists all menu catalogues.
 */
public interface GetMenuCataloguesQuery {
    /**
     * @return every catalogue, in no particular order
     */
    List<MenuCatalogue> query();
}
