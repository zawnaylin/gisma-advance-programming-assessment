package com.qr_restaurant.menu.repository.read;

import com.qr_restaurant.menu.entities.Category;
import com.qr_restaurant.menu.vo.CategoryId;

import java.util.List;
import java.util.Optional;

public interface CategoryReadRepository {

    List<Category> findAll();

    Optional<Category> findById(CategoryId id);
}
