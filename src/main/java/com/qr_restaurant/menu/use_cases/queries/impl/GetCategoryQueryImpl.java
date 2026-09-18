package com.qr_restaurant.menu.use_cases.queries.impl;

import com.qr_restaurant.menu.entities.Category;
import com.qr_restaurant.menu.repository.read.CategoryReadRepository;
import com.qr_restaurant.menu.use_cases.queries.GetCategoryQuery;
import com.qr_restaurant.menu.vo.CategoryId;
import org.springframework.stereotype.Service;

@Service
class GetCategoryQueryImpl implements GetCategoryQuery {

    private final CategoryReadRepository categoryReadRepository;

    GetCategoryQueryImpl(CategoryReadRepository categoryReadRepository) {
        this.categoryReadRepository = categoryReadRepository;
    }

    @Override
    public Category query(CategoryId id) {
        return categoryReadRepository.findById(id).orElse(null);
    }
}
