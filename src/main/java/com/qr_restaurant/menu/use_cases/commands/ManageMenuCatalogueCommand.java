package com.qr_restaurant.menu.use_cases.commands;

import com.qr_restaurant.menu.use_cases.commands.dtos.MenuCatalogueDetails;
import com.qr_restaurant.menu.vo.MenuCatalogueId;

/**
 * Creates, updates and deletes menu catalogues (the menus that apply at certain times).
 */
public interface ManageMenuCatalogueCommand {
    /**
     * Creates a catalogue.
     *
     * @param id      the id to use, or {@code null} to generate one
     * @param details name and description
     * @return the id of the new catalogue
     * @throws IllegalStateException if a catalogue with that id already exists
     */
    MenuCatalogueId create(MenuCatalogueId id, MenuCatalogueDetails details);

    /**
     * Replaces a catalogue's name and description.
     *
     * @throws java.util.NoSuchElementException if the catalogue doesn't exist
     */
    void update(MenuCatalogueId id, MenuCatalogueDetails details);

    /**
     * Deletes a catalogue.
     *
     * @throws java.util.NoSuchElementException if the catalogue doesn't exist
     * @throws IllegalStateException           if menu items still belong to it
     */
    void delete(MenuCatalogueId id);
}
