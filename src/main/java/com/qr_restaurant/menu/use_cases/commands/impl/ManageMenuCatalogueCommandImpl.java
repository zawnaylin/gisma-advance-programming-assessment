package com.qr_restaurant.menu.use_cases.commands.impl;

import com.qr_restaurant.menu.entities.MenuCatalogue;
import com.qr_restaurant.menu.repository.MenuCatalogueRepository;
import com.qr_restaurant.menu.repository.read.MenuCatalogueReadRepository;
import com.qr_restaurant.menu.repository.read.MenuItemReadRepository;
import com.qr_restaurant.menu.use_cases.commands.ManageMenuCatalogueCommand;
import com.qr_restaurant.menu.use_cases.commands.dtos.MenuCatalogueDetails;
import com.qr_restaurant.menu.vo.MenuCatalogueId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Transactional
class ManageMenuCatalogueCommandImpl implements ManageMenuCatalogueCommand {

    private final MenuCatalogueRepository menuCatalogueRepository;
    private final MenuCatalogueReadRepository menuCatalogueReadRepository;
    private final MenuItemReadRepository menuItemReadRepository;

    ManageMenuCatalogueCommandImpl(MenuCatalogueRepository menuCatalogueRepository,
                                   MenuCatalogueReadRepository menuCatalogueReadRepository,
                                   MenuItemReadRepository menuItemReadRepository) {
        this.menuCatalogueRepository = menuCatalogueRepository;
        this.menuCatalogueReadRepository = menuCatalogueReadRepository;
        this.menuItemReadRepository = menuItemReadRepository;
    }

    @Override
    public MenuCatalogueId create(MenuCatalogueId id, MenuCatalogueDetails details) {
        var catalogueId = id != null ? id : new MenuCatalogueId(UUID.randomUUID().toString());
        // save() would silently overwrite an existing row with the same id.
        if (menuCatalogueReadRepository.findById(catalogueId).isPresent()) {
            throw new IllegalStateException("Catalogue already exists: " + catalogueId.value());
        }

        menuCatalogueRepository.save(new MenuCatalogue(catalogueId, details.name(), details.description()));
        return catalogueId;
    }

    @Override
    public void update(MenuCatalogueId id, MenuCatalogueDetails details) {
        var catalogue = menuCatalogueReadRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No such catalogue: " + id.value()));

        catalogue.update(details.name(), details.description());
        menuCatalogueRepository.save(catalogue);
    }

    @Override
    public void delete(MenuCatalogueId id) {
        if (menuCatalogueReadRepository.findById(id).isEmpty()) {
            throw new NoSuchElementException("No such catalogue: " + id.value());
        }
        var items = menuItemReadRepository.findByCatalogueAndCategory(id, null).size();
        if (items > 0) {
            throw new IllegalStateException("Catalogue " + id.value() + " still has " + items
                    + " menu item(s); delete or move them first");
        }

        menuCatalogueRepository.deleteById(id);
    }
}
