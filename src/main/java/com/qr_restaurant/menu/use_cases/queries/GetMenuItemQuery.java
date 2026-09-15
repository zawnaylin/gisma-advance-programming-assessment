package com.qr_restaurant.menu.use_cases.queries;

import com.qr_restaurant.menu.use_cases.queries.dtos.MenuItemView;
import com.qr_restaurant.menu.vo.MenuItemId;

public interface GetMenuItemQuery {
    MenuItemView query(MenuItemId id);
}
