package com.qr_restaurant.menu.entities;

import com.qr_restaurant.common.Domain;
import com.qr_restaurant.menu.vo.CatalogueId;
import com.qr_restaurant.menu.vo.MenuItemId;

public class MenuItem extends Domain<MenuItemId> {
    private String name;
    private String description;
    private double price;
    private CatalogueId catalogueId;

    private Catalogue catalogue;
}
