package com.qr_restaurant.menu.entities;

import com.qr_restaurant.common.Domain;
import com.qr_restaurant.menu.vo.MenuCatalogueId;
import lombok.Getter;

@Getter
public class MenuCatalogue extends Domain<MenuCatalogueId> {
    private String name;
    private String description;

    public MenuCatalogue(MenuCatalogueId id, String name, String description) {
        super(id);
        this.name = name;
        this.description = description;
    }
}
