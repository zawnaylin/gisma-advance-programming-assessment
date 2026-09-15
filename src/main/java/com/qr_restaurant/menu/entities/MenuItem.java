package com.qr_restaurant.menu.entities;

import com.qr_restaurant.common.Domain;
import com.qr_restaurant.menu.vo.CategoryId;
import com.qr_restaurant.menu.vo.MenuCatalogueId;
import com.qr_restaurant.menu.vo.MenuItemId;
import lombok.Getter;

@Getter
public class MenuItem extends Domain<MenuItemId> {
    private String name;
    private String description;
    private double price;
    private MenuCatalogueId catalogueId;
    private CategoryId categoryId;

    public MenuItem(MenuItemId id, String name, String description, double price,
                     MenuCatalogueId catalogueId, CategoryId categoryId) {
        super(id);
        this.name = name;
        this.description = description;
        this.price = price;
        this.catalogueId = catalogueId;
        this.categoryId = categoryId;
    }
}
