package com.qr_restaurant.menu.use_cases.commands.impl;

import com.qr_restaurant.menu.entities.Category;
import com.qr_restaurant.menu.repository.CategoryRepository;
import com.qr_restaurant.menu.repository.read.CategoryReadRepository;
import com.qr_restaurant.menu.repository.read.MenuItemReadRepository;
import com.qr_restaurant.menu.use_cases.commands.ManageCategoryCommand;
import com.qr_restaurant.menu.use_cases.commands.dtos.CategoryDetails;
import com.qr_restaurant.menu.vo.CategoryId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Transactional
class ManageCategoryCommandImpl implements ManageCategoryCommand {

    private final CategoryRepository categoryRepository;
    private final CategoryReadRepository categoryReadRepository;
    private final MenuItemReadRepository menuItemReadRepository;

    ManageCategoryCommandImpl(CategoryRepository categoryRepository,
                              CategoryReadRepository categoryReadRepository,
                              MenuItemReadRepository menuItemReadRepository) {
        this.categoryRepository = categoryRepository;
        this.categoryReadRepository = categoryReadRepository;
        this.menuItemReadRepository = menuItemReadRepository;
    }

    @Override
    public CategoryId create(CategoryId id, CategoryDetails details) {
        var categoryId = id != null ? id : new CategoryId(UUID.randomUUID().toString());
        // save() would silently overwrite an existing row with the same id.
        if (categoryReadRepository.findById(categoryId).isPresent()) {
            throw new IllegalStateException("Category already exists: " + categoryId.value());
        }

        categoryRepository.save(new Category(categoryId, details.name(), details.description()));
        return categoryId;
    }

    @Override
    public void update(CategoryId id, CategoryDetails details) {
        var category = categoryReadRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No such category: " + id.value()));

        category.update(details.name(), details.description());
        categoryRepository.save(category);
    }

    @Override
    public void delete(CategoryId id) {
        if (categoryReadRepository.findById(id).isEmpty()) {
            throw new NoSuchElementException("No such category: " + id.value());
        }
        var items = menuItemReadRepository.findByCatalogueAndCategory(null, id).size();
        if (items > 0) {
            throw new IllegalStateException("Category " + id.value() + " still has " + items
                    + " menu item(s); delete or move them first");
        }

        categoryRepository.deleteById(id);
    }
}
