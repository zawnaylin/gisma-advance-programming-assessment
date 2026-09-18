package com.qr_restaurant.menu.repository.read;

import com.qr_restaurant.menu.entities.MenuItem;
import com.qr_restaurant.menu.vo.CategoryId;
import com.qr_restaurant.menu.vo.MenuCatalogueId;
import com.qr_restaurant.menu.vo.MenuItemId;

import java.util.List;
import java.util.Optional;

/**
 * Reads menuitems. Commands and queries depend on this rather than on Spring Data,
 * so the persistence choice stays behind the repository.
 */
public interface MenuItemReadRepository {

    /**
     * @return every one of them, in no particular order
     */
    List<MenuItem> findAll();

    /**
     * @param id the id to look for
     * @return the matching one, or empty if there is none
     */
    Optional<MenuItem> findById(MenuItemId id);

    /**
     * @param catalogueId only items in this catalogue, or {@code null} for any
     * @param categoryId  only items in this category, or {@code null} for any
     * @return the matching items
     */
    List<MenuItem> findByCatalogueAndCategory(MenuCatalogueId catalogueId, CategoryId categoryId);
}
