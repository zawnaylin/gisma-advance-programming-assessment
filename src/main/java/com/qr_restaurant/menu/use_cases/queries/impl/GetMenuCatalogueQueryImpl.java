package com.qr_restaurant.menu.use_cases.queries.impl;

import com.qr_restaurant.menu.entities.MenuCatalogue;
import com.qr_restaurant.menu.repository.read.MenuCatalogueReadRepository;
import com.qr_restaurant.menu.use_cases.queries.GetMenuCatalogueQuery;
import com.qr_restaurant.menu.vo.MenuCatalogueId;
import org.springframework.stereotype.Service;

@Service
class GetMenuCatalogueQueryImpl implements GetMenuCatalogueQuery {

    private final MenuCatalogueReadRepository menuCatalogueReadRepository;

    GetMenuCatalogueQueryImpl(MenuCatalogueReadRepository menuCatalogueReadRepository) {
        this.menuCatalogueReadRepository = menuCatalogueReadRepository;
    }

    @Override
    public MenuCatalogue query(MenuCatalogueId id) {
        return menuCatalogueReadRepository.findById(id).orElse(null);
    }
}
