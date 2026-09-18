package com.qr_restaurant.menu.use_cases.queries;

import com.qr_restaurant.menu.entities.MenuCatalogue;
import com.qr_restaurant.menu.vo.MenuCatalogueId;

/**
 * Looks up a single menu catalogue.
 */
public interface GetMenuCatalogueQuery {
    /**
     * @param id the catalogue to look up
     * @return the catalogue, or {@code null} if there is none with that id
     */
    MenuCatalogue query(MenuCatalogueId id);
}
