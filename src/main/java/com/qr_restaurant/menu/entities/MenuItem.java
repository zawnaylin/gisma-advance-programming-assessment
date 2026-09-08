package com.qr_restaurant.menu.entities;

import com.qr_restaurant.common.Domain;
import com.qr_restaurant.menu.vo.MenuCatalogueId;
import com.qr_restaurant.menu.vo.CategoryId;
import com.qr_restaurant.menu.vo.MenuItemId;

public class MenuItem extends Domain<MenuItemId> {
    private String name;
    private String description;
    private double price;
    private MenuCatalogueId catalogueId;
    private CategoryId categoryId;

    private MenuCatalogue catalogue;
    private Category category;
}
