package com.qr_restaurant.menu.repository.impl;

import com.qr_restaurant.menu.entities.MenuCatalogue;
import com.qr_restaurant.menu.repository.MenuCatalogueRepository;
import com.qr_restaurant.menu.repository.read.MenuCatalogueReadRepository;
import com.qr_restaurant.menu.vo.MenuCatalogueId;
import org.springframework.data.jpa.repository.JpaRepository;

interface JpaMenuCatalogueRepository extends JpaRepository<MenuCatalogue, MenuCatalogueId>,
        MenuCatalogueRepository, MenuCatalogueReadRepository {
}
