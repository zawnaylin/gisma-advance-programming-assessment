package com.qr_restaurant.menu.repository.read;

import com.qr_restaurant.menu.entities.MenuItem;
import com.qr_restaurant.menu.vo.CategoryId;
import com.qr_restaurant.menu.vo.MenuCatalogueId;
import com.qr_restaurant.menu.vo.MenuItemId;

import java.util.List;
import java.util.Optional;

public interface MenuItemReadRepository {

    List<MenuItem> findAll();

    Optional<MenuItem> findById(MenuItemId id);

    List<MenuItem> findByCatalogueAndCategory(MenuCatalogueId catalogueId, CategoryId categoryId);
}
