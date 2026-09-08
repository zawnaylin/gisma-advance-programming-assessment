package com.qr_restaurant.menu.use_cases.queries;

import com.qr_restaurant.menu.entities.Category;

import java.util.List;

public interface GetCategoriesQuery {
    List<Category> query();
}
