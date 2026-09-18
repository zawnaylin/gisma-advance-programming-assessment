package com.qr_restaurant.menu.repository.impl;

import com.qr_restaurant.menu.entities.Category;
import com.qr_restaurant.menu.repository.CategoryRepository;
import com.qr_restaurant.menu.repository.read.CategoryReadRepository;
import com.qr_restaurant.menu.vo.CategoryId;
import org.springframework.data.jpa.repository.JpaRepository;

interface JpaCategoryRepository extends JpaRepository<Category, CategoryId>,
        CategoryRepository, CategoryReadRepository {
}
