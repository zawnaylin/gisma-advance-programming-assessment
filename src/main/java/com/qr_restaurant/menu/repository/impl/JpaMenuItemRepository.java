package com.qr_restaurant.menu.repository.impl;

import com.qr_restaurant.menu.entities.MenuItem;
import com.qr_restaurant.menu.repository.MenuItemRepository;
import com.qr_restaurant.menu.repository.read.MenuItemReadRepository;
import com.qr_restaurant.menu.vo.CategoryId;
import com.qr_restaurant.menu.vo.MenuCatalogueId;
import com.qr_restaurant.menu.vo.MenuItemId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface JpaMenuItemRepository extends JpaRepository<MenuItem, MenuItemId>,
        MenuItemRepository, MenuItemReadRepository {

    List<MenuItem> findByCatalogueId(MenuCatalogueId catalogueId);

    List<MenuItem> findByCategoryId(CategoryId categoryId);

    List<MenuItem> findByCatalogueIdAndCategoryId(MenuCatalogueId catalogueId, CategoryId categoryId);

    @Override
    default List<MenuItem> findByCatalogueAndCategory(MenuCatalogueId catalogueId, CategoryId categoryId) {
        if (catalogueId == null && categoryId == null) {
            return findAll();
        }
        if (catalogueId == null) {
            return findByCategoryId(categoryId);
        }
        if (categoryId == null) {
            return findByCatalogueId(catalogueId);
        }
        return findByCatalogueIdAndCategoryId(catalogueId, categoryId);
    }
}
