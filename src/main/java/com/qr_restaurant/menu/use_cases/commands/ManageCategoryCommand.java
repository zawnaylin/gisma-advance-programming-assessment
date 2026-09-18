package com.qr_restaurant.menu.use_cases.commands;

import com.qr_restaurant.menu.use_cases.commands.dtos.CategoryDetails;
import com.qr_restaurant.menu.vo.CategoryId;

/**
 * Creates, updates and deletes menu categories (groupings such as "Desserts" that are
 * reused across catalogues).
 */
public interface ManageCategoryCommand {
    /**
     * Creates a category.
     *
     * @param id      the id to use, or {@code null} to generate one
     * @param details name and description
     * @return the id of the new category
     * @throws IllegalStateException if a category with that id already exists
     */
    CategoryId create(CategoryId id, CategoryDetails details);

    /**
     * Replaces a category's name and description.
     *
     * @throws java.util.NoSuchElementException if the category doesn't exist
     */
    void update(CategoryId id, CategoryDetails details);

    /**
     * Deletes a category.
     *
     * @throws java.util.NoSuchElementException if the category doesn't exist
     * @throws IllegalStateException           if menu items are still in it
     */
    void delete(CategoryId id);
}
