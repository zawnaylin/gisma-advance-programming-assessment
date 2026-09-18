package com.qr_restaurant.menu.use_cases.queries;

import com.qr_restaurant.menu.entities.Category;

import java.util.List;

/**
 * Lists all menu categories.
 */
public interface GetCategoriesQuery {
    /**
     * @return every category, in no particular order
     */
    List<Category> query();
}
