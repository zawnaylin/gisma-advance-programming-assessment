package com.qr_restaurant.menu.repository;

import com.qr_restaurant.menu.entities.Category;
import com.qr_restaurant.menu.vo.CategoryId;

/**
 * Stores menu categories.
 */
public interface CategoryRepository {

    /**
     * Inserts a new category or saves changes to an existing one.
     *
     * @return the stored category
     */
    Category save(Category category);

    /**
     * Removes a category. Does nothing if it is already gone.
     */
    void deleteById(CategoryId id);
}
