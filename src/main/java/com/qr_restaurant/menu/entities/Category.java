package com.qr_restaurant.menu.entities;

import com.qr_restaurant.common.Domain;
import com.qr_restaurant.menu.vo.CategoryId;
import jakarta.persistence.Entity;
import lombok.Getter;

/**
 * A grouping of menu items, e.g. "Desserts". Categories are reused across catalogues.
 */
@Getter
@Entity
@jakarta.persistence.Table(name = "categories")
public class Category extends Domain<CategoryId> {
    private String name;
    private String description;

    protected Category() {
    }

    public Category(CategoryId id, String name, String description) {
        super(id);
        this.name = name;
        this.description = description;
    }

    /**
     * Replaces the category's name and description.
     */
    public void update(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
