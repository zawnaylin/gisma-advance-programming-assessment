package com.qr_restaurant.menu.entities;

import com.qr_restaurant.common.Domain;
import com.qr_restaurant.menu.vo.MenuCatalogueId;

import java.util.List;

public class MenuCatalogue extends Domain<MenuCatalogueId> {
    private String name;
    private String description;

    private List<MenuItem> menuItems;
}
