package com.qr_restaurant.menu.repository.impl;

import com.qr_restaurant.common.InMemoryRepository;
import com.qr_restaurant.menu.entities.MenuCatalogue;
import com.qr_restaurant.menu.repository.MenuCatalogueRepository;
import com.qr_restaurant.menu.repository.read.MenuCatalogueReadRepository;
import com.qr_restaurant.menu.vo.MenuCatalogueId;
import org.springframework.stereotype.Repository;

@Repository
class MenuCatalogueRepositoryImpl extends InMemoryRepository<MenuCatalogueId, MenuCatalogue>
        implements MenuCatalogueRepository, MenuCatalogueReadRepository {
}
