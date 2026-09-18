package com.qr_restaurant.menu.repository.read;

import com.qr_restaurant.menu.entities.MenuCatalogue;
import com.qr_restaurant.menu.vo.MenuCatalogueId;

import java.util.List;
import java.util.Optional;

/**
 * Reads menucatalogues. Commands and queries depend on this rather than on Spring Data,
 * so the persistence choice stays behind the repository.
 */
public interface MenuCatalogueReadRepository {

    /**
     * @return every one of them, in no particular order
     */
    List<MenuCatalogue> findAll();

    /**
     * @param id the id to look for
     * @return the matching one, or empty if there is none
     */
    Optional<MenuCatalogue> findById(MenuCatalogueId id);
}
