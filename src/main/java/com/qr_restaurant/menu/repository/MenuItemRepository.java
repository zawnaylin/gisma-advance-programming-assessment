package com.qr_restaurant.menu.repository;

import com.qr_restaurant.menu.entities.MenuItem;
import com.qr_restaurant.menu.vo.MenuItemId;

/**
 * Stores menu items.
 */
public interface MenuItemRepository {

    /**
     * Inserts a new item or saves changes to an existing one.
     *
     * @return the stored item
     */
    MenuItem save(MenuItem item);

    /**
     * Removes a menu item. Does nothing if it is already gone.
     */
    void deleteById(MenuItemId id);
}
