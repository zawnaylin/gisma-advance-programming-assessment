package com.qr_restaurant.menu.entities;

import com.qr_restaurant.common.Domain;
import com.qr_restaurant.menu.vo.CategoryId;
import com.qr_restaurant.menu.vo.MenuCatalogueId;
import com.qr_restaurant.menu.vo.MenuItemId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.Getter;

/**
 * Something a customer can order. It refers to its catalogue and category by id rather
 * than being owned by them.
 */
@Getter
@Entity
@jakarta.persistence.Table(name = "menu_items")
public class MenuItem extends Domain<MenuItemId> {
    private String name;
    private String description;
    private double price;
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "catalogue_id"))
    private MenuCatalogueId catalogueId;
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "category_id"))
    private CategoryId categoryId;

    protected MenuItem() {
    }

    public MenuItem(MenuItemId id, String name, String description, double price,
                     MenuCatalogueId catalogueId, CategoryId categoryId) {
        super(id);
        this.name = name;
        this.description = description;
        this.price = price;
        this.catalogueId = catalogueId;
        this.categoryId = categoryId;
    }

    /**
     * Replaces all of the item's details, including which catalogue and category it is in.
     * Orders already placed keep the price they were placed at.
     */
    public void update(String name, String description, double price,
                       MenuCatalogueId catalogueId, CategoryId categoryId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.catalogueId = catalogueId;
        this.categoryId = categoryId;
    }
}
