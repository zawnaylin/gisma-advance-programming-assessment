package com.qr_restaurant.menu.use_cases.queries.impl;

import com.qr_restaurant.menu.repository.read.MenuItemReadRepository;
import com.qr_restaurant.menu.use_cases.queries.GetMenuItemQuery;
import com.qr_restaurant.menu.use_cases.queries.dtos.MenuItemView;
import com.qr_restaurant.menu.vo.MenuItemId;
import org.springframework.stereotype.Service;

@Service
class GetMenuItemQueryImpl implements GetMenuItemQuery {

    private final MenuItemReadRepository menuItemReadRepository;

    GetMenuItemQueryImpl(MenuItemReadRepository menuItemReadRepository) {
        this.menuItemReadRepository = menuItemReadRepository;
    }

    @Override
    public MenuItemView query(MenuItemId id) {
        return menuItemReadRepository.findById(id)
                .map(item -> new MenuItemView(
                        item.getId(), item.getName(), item.getDescription(), item.getPrice(),
                        item.getCatalogueId(), item.getCategoryId()
                ))
                .orElse(null);
    }
}
