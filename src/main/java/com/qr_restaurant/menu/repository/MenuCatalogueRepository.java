package com.qr_restaurant.menu.repository;

import com.qr_restaurant.menu.entities.MenuCatalogue;
import com.qr_restaurant.menu.vo.MenuCatalogueId;

/**
 * Stores menu catalogues.
 */
public interface MenuCatalogueRepository {

    /**
     * Inserts a new catalogue or saves changes to an existing one.
     *
     * @return the stored catalogue
     */
    MenuCatalogue save(MenuCatalogue catalogue);

    /**
     * Removes a catalogue. Does nothing if it is already gone.
     */
    void deleteById(MenuCatalogueId id);
}
