package com.qr_restaurant.menu.repository.read;

import com.qr_restaurant.menu.entities.Category;
import com.qr_restaurant.menu.vo.CategoryId;

import java.util.List;
import java.util.Optional;

/**
 * Reads categorys. Commands and queries depend on this rather than on Spring Data,
 * so the persistence choice stays behind the repository.
 */
public interface CategoryReadRepository {

    /**
     * @return every one of them, in no particular order
     */
    List<Category> findAll();

    /**
     * @param id the id to look for
     * @return the matching one, or empty if there is none
     */
    Optional<Category> findById(CategoryId id);
}
