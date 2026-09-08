package com.qr_restaurant.menu.entities;

import com.qr_restaurant.common.Domain;
import com.qr_restaurant.menu.vo.CategoryId;

import java.util.List;

public class Category extends Domain<CategoryId> {
    private String name;
    private String description;

    private List<MenuItem> menuItems;
}
