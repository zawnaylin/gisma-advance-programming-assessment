package com.qr_restaurant.menu.web.controllers;

import com.qr_restaurant.menu.entities.Category;
import com.qr_restaurant.menu.use_cases.commands.ManageCategoryCommand;
import com.qr_restaurant.menu.use_cases.commands.dtos.CategoryDetails;
import com.qr_restaurant.menu.use_cases.queries.GetCategoriesQuery;
import com.qr_restaurant.menu.use_cases.queries.GetCategoryQuery;
import com.qr_restaurant.menu.vo.CategoryId;
import com.qr_restaurant.menu.web.dtos.requests.CategoryRequestDto;
import com.qr_restaurant.menu.web.dtos.responses.CategoryResponseDto;
import com.qr_restaurant.menu.web.dtos.responses.GetCategoriesResponseDto;
import com.qr_restaurant.menu.web.dtos.responses.common.CategoryDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * REST API for menu categories. See docs/rest-api.md.
 */
@RestController
@RequestMapping("/api/menu/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final GetCategoriesQuery getCategoriesQuery;
    private final GetCategoryQuery getCategoryQuery;
    private final ManageCategoryCommand manageCategoryCommand;

    private final Converter<Category, CategoryDto> categoryDtoConverter;

    /**
     * @return all menu categories
     */
    @GetMapping
    public ResponseEntity<GetCategoriesResponseDto> getCategories() {
        var body = getCategoriesQuery.query().stream().map(categoryDtoConverter::convert).toList();
        return ResponseEntity.ok(new GetCategoriesResponseDto(body));
    }

    /**
     * @return the category, or 404 if there is none with that id
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> getCategory(@PathVariable String id) {
        var category = getCategoryQuery.query(new CategoryId(id));
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new CategoryResponseDto(categoryDtoConverter.convert(category)));
    }

    /**
     * Creates a category; the body's id is optional and generated when absent.
     *
     * @return 201 with a Location header, or 400 for an invalid body and 409 if the id is taken
     */
    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(@Valid @RequestBody CategoryRequestDto request) {
        var id = manageCategoryCommand.create(
                request.id() == null ? null : new CategoryId(request.id()),
                new CategoryDetails(request.name(), request.description()));

        var body = categoryDtoConverter.convert(getCategoryQuery.query(id));
        return ResponseEntity.created(URI.create("/api/menu/categories/" + id.value()))
                .body(new CategoryResponseDto(body));
    }

    /**
     * Replaces a category's details. Never creates one.
     *
     * @return 200 with the updated resource, 400 for an invalid body, or 404 if it doesn't exist
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> updateCategory(@PathVariable String id,
                                                              @Valid @RequestBody CategoryRequestDto request) {
        var categoryId = new CategoryId(id);
        manageCategoryCommand.update(categoryId, new CategoryDetails(request.name(), request.description()));

        var body = categoryDtoConverter.convert(getCategoryQuery.query(categoryId));
        return ResponseEntity.ok(new CategoryResponseDto(body));
    }

    /**
     * Deletes a category.
     *
     * @return 204, 404 if it doesn't exist, or 409 if menu items are still in it
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        manageCategoryCommand.delete(new CategoryId(id));
        return ResponseEntity.noContent().build();
    }
}
