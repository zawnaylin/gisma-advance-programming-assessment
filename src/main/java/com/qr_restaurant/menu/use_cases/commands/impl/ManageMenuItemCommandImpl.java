package com.qr_restaurant.menu.use_cases.commands.impl;

import com.qr_restaurant.menu.entities.MenuItem;
import com.qr_restaurant.menu.repository.MenuItemRepository;
import com.qr_restaurant.menu.repository.read.CategoryReadRepository;
import com.qr_restaurant.menu.repository.read.MenuCatalogueReadRepository;
import com.qr_restaurant.menu.repository.read.MenuItemReadRepository;
import com.qr_restaurant.menu.use_cases.commands.ManageMenuItemCommand;
import com.qr_restaurant.menu.use_cases.commands.dtos.MenuItemDetails;
import com.qr_restaurant.menu.vo.MenuItemId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Transactional
class ManageMenuItemCommandImpl implements ManageMenuItemCommand {

    private final MenuItemRepository menuItemRepository;
    private final MenuItemReadRepository menuItemReadRepository;
    private final MenuCatalogueReadRepository menuCatalogueReadRepository;
    private final CategoryReadRepository categoryReadRepository;

    ManageMenuItemCommandImpl(MenuItemRepository menuItemRepository,
                              MenuItemReadRepository menuItemReadRepository,
                              MenuCatalogueReadRepository menuCatalogueReadRepository,
                              CategoryReadRepository categoryReadRepository) {
        this.menuItemRepository = menuItemRepository;
        this.menuItemReadRepository = menuItemReadRepository;
        this.menuCatalogueReadRepository = menuCatalogueReadRepository;
        this.categoryReadRepository = categoryReadRepository;
    }

    @Override
    public MenuItemId create(MenuItemId id, MenuItemDetails details) {
        var itemId = id != null ? id : new MenuItemId(UUID.randomUUID().toString());
        // save() would silently overwrite an existing row with the same id.
        if (menuItemReadRepository.findById(itemId).isPresent()) {
            throw new IllegalStateException("Menu item already exists: " + itemId.value());
        }
        requireReferencesExist(details);

        menuItemRepository.save(new MenuItem(itemId, details.name(), details.description(), details.price(),
                details.catalogueId(), details.categoryId()));
        return itemId;
    }

    @Override
    public void update(MenuItemId id, MenuItemDetails details) {
        var item = menuItemReadRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No such menu item: " + id.value()));
        requireReferencesExist(details);

        item.update(details.name(), details.description(), details.price(), details.catalogueId(), details.categoryId());
        menuItemRepository.save(item);
    }

    @Override
    public void delete(MenuItemId id) {
        if (menuItemReadRepository.findById(id).isEmpty()) {
            throw new NoSuchElementException("No such menu item: " + id.value());
        }
        menuItemRepository.deleteById(id);
    }

    private void requireReferencesExist(MenuItemDetails details) {
        if (menuCatalogueReadRepository.findById(details.catalogueId()).isEmpty()) {
            throw new IllegalArgumentException("No such catalogue: " + details.catalogueId().value());
        }
        if (categoryReadRepository.findById(details.categoryId()).isEmpty()) {
            throw new IllegalArgumentException("No such category: " + details.categoryId().value());
        }
    }
}
