package com.qr_restaurant.menu.use_cases.queries;

import com.qr_restaurant.menu.use_cases.queries.dtos.MenuItemView;
import com.qr_restaurant.menu.vo.MenuItemId;

/**
 * Looks up a single menu item. Used by the order module to price an order line.
 */
public interface GetMenuItemQuery {
    /**
     * @param id the item to look up
     * @return the item, or {@code null} if there is none with that id
     */
    MenuItemView query(MenuItemId id);
}
