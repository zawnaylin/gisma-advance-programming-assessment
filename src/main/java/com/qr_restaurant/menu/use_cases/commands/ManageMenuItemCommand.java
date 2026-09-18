package com.qr_restaurant.menu.use_cases.commands;

import com.qr_restaurant.menu.use_cases.commands.dtos.MenuItemDetails;
import com.qr_restaurant.menu.vo.MenuItemId;

/**
 * Creates, updates and deletes menu items.
 */
public interface ManageMenuItemCommand {
    /**
     * Creates a menu item in an existing catalogue and category.
     *
     * @param id      the id to use, or {@code null} to generate one
     * @param details name, description, price and the catalogue and category it belongs to
     * @return the id of the new item
     * @throws IllegalStateException    if an item with that id already exists
     * @throws IllegalArgumentException if the catalogue or category doesn't exist
     */
    MenuItemId create(MenuItemId id, MenuItemDetails details);

    /**
     * Replaces all of an item's details, including which catalogue and category it belongs to.
     * Orders already placed keep the price they were placed at.
     *
     * @throws java.util.NoSuchElementException if the item doesn't exist
     * @throws IllegalArgumentException         if the catalogue or category doesn't exist
     */
    void update(MenuItemId id, MenuItemDetails details);

    /**
     * Deletes a menu item. Past orders keep its id and the price they were placed at, so they
     * stay readable; screens fall back to showing the id where the name is gone.
     *
     * @throws java.util.NoSuchElementException if the item doesn't exist
     */
    void delete(MenuItemId id);
}
