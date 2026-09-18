package com.qr_restaurant.menu.use_cases.queries;

import com.qr_restaurant.menu.entities.Category;
import com.qr_restaurant.menu.vo.CategoryId;

/**
 * Looks up a single menu category.
 */
public interface GetCategoryQuery {
    /**
     * @param id the category to look up
     * @return the category, or {@code null} if there is none with that id
     */
    Category query(CategoryId id);
}
