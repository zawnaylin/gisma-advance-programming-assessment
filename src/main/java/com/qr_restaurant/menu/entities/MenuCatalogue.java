package com.qr_restaurant.menu.entities;

import com.qr_restaurant.common.Domain;
import com.qr_restaurant.menu.vo.MenuCatalogueId;
import jakarta.persistence.Entity;
import lombok.Getter;

/**
 * A menu that applies at certain times, e.g. "All Day Menu".
 */
@Getter
@Entity
@jakarta.persistence.Table(name = "menu_catalogues")
public class MenuCatalogue extends Domain<MenuCatalogueId> {
    private String name;
    private String description;

    protected MenuCatalogue() {
    }

    public MenuCatalogue(MenuCatalogueId id, String name, String description) {
        super(id);
        this.name = name;
        this.description = description;
    }

    /**
     * Replaces the catalogue's name and description.
     */
    public void update(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
