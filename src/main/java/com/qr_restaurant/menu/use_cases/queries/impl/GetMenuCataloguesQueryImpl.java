package com.qr_restaurant.menu.use_cases.queries.impl;

import com.qr_restaurant.menu.entities.MenuCatalogue;
import com.qr_restaurant.menu.repository.read.MenuCatalogueReadRepository;
import com.qr_restaurant.menu.use_cases.queries.GetMenuCataloguesQuery;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class GetMenuCataloguesQueryImpl implements GetMenuCataloguesQuery {

    private final MenuCatalogueReadRepository menuCatalogueReadRepository;

    GetMenuCataloguesQueryImpl(MenuCatalogueReadRepository menuCatalogueReadRepository) {
        this.menuCatalogueReadRepository = menuCatalogueReadRepository;
    }

    @Override
    public List<MenuCatalogue> query() {
        return menuCatalogueReadRepository.findAll();
    }
}
