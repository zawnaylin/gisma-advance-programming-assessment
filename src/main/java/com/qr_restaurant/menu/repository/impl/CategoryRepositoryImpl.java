package com.qr_restaurant.menu.repository.impl;

import com.qr_restaurant.common.InMemoryRepository;
import com.qr_restaurant.menu.entities.Category;
import com.qr_restaurant.menu.repository.CategoryRepository;
import com.qr_restaurant.menu.repository.read.CategoryReadRepository;
import com.qr_restaurant.menu.vo.CategoryId;
import org.springframework.stereotype.Repository;

@Repository
class CategoryRepositoryImpl extends InMemoryRepository<CategoryId, Category>
        implements CategoryRepository, CategoryReadRepository {
}
