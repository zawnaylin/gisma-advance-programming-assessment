package com.qr_restaurant.menu.use_cases.queries.impl;

import com.qr_restaurant.menu.repository.read.MenuItemReadRepository;
import com.qr_restaurant.menu.use_cases.queries.GetMenuItemsQuery;
import com.qr_restaurant.menu.use_cases.queries.dtos.MenuItemView;
import com.qr_restaurant.menu.vo.CategoryId;
import com.qr_restaurant.menu.vo.MenuCatalogueId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class GetMenuItemsQueryImpl implements GetMenuItemsQuery {

    private final MenuItemReadRepository menuItemReadRepository;

    GetMenuItemsQueryImpl(MenuItemReadRepository menuItemReadRepository) {
        this.menuItemReadRepository = menuItemReadRepository;
    }

    @Override
    public List<MenuItemView> query(MenuCatalogueId catalogueId, CategoryId categoryId) {
        return menuItemReadRepository.findByCatalogueAndCategory(catalogueId, categoryId).stream()
                .map(item -> new MenuItemView(
                        item.getId(), item.getName(), item.getDescription(), item.getPrice(),
                        item.getCatalogueId(), item.getCategoryId()
                ))
                .toList();
    }
}
