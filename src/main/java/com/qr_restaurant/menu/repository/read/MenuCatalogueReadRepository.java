package com.qr_restaurant.menu.repository.read;

import com.qr_restaurant.menu.entities.MenuCatalogue;
import com.qr_restaurant.menu.vo.MenuCatalogueId;

import java.util.List;
import java.util.Optional;

public interface MenuCatalogueReadRepository {

    List<MenuCatalogue> findAll();

    Optional<MenuCatalogue> findById(MenuCatalogueId id);
}
