package com.qr_restaurant.menu.entities;

import com.qr_restaurant.common.Domain;
import com.qr_restaurant.menu.vo.CatalogueId;

import java.util.List;

public class Catalogue extends Domain<CatalogueId> {
    private String name;
    private String description;

    private List<MenuItem> menuItems;

    // TODO: revise this
    public void addMenuItem(MenuItem menuItem) {
        if (menuItems == null) {
            menuItems = List.of(menuItem);
        } else {
            this.menuItems.add(menuItem);
        }
    }
}
