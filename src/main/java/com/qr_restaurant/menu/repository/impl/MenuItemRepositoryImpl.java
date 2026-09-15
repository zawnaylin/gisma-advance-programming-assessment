package com.qr_restaurant.menu.repository.impl;

import com.qr_restaurant.common.InMemoryRepository;
import com.qr_restaurant.menu.entities.MenuItem;
import com.qr_restaurant.menu.repository.MenuItemRepository;
import com.qr_restaurant.menu.repository.read.MenuItemReadRepository;
import com.qr_restaurant.menu.vo.CategoryId;
import com.qr_restaurant.menu.vo.MenuCatalogueId;
import com.qr_restaurant.menu.vo.MenuItemId;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
class MenuItemRepositoryImpl extends InMemoryRepository<MenuItemId, MenuItem>
        implements MenuItemRepository, MenuItemReadRepository {

    @Override
    public List<MenuItem> findByCatalogueAndCategory(MenuCatalogueId catalogueId, CategoryId categoryId) {
        return findAll().stream()
                .filter(item -> catalogueId == null || catalogueId.equals(item.getCatalogueId()))
                .filter(item -> categoryId == null || categoryId.equals(item.getCategoryId()))
                .toList();
    }
}
