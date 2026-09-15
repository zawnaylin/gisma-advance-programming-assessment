package com.qr_restaurant.menu.use_cases.queries.impl;

import com.qr_restaurant.menu.entities.Category;
import com.qr_restaurant.menu.repository.read.CategoryReadRepository;
import com.qr_restaurant.menu.use_cases.queries.GetCategoriesQuery;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class GetCategoriesQueryImpl implements GetCategoriesQuery {

    private final CategoryReadRepository categoryReadRepository;

    GetCategoriesQueryImpl(CategoryReadRepository categoryReadRepository) {
        this.categoryReadRepository = categoryReadRepository;
    }

    @Override
    public List<Category> query() {
        return categoryReadRepository.findAll();
    }
}
