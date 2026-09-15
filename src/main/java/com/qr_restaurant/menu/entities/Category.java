package com.qr_restaurant.menu.entities;

import com.qr_restaurant.common.Domain;
import com.qr_restaurant.menu.vo.CategoryId;
import lombok.Getter;

@Getter
public class Category extends Domain<CategoryId> {
    private String name;
    private String description;

    public Category(CategoryId id, String name, String description) {
        super(id);
        this.name = name;
        this.description = description;
    }
}
