package com.qr_restaurant.menu.web.controllers;

import com.qr_restaurant.menu.entities.Category;
import com.qr_restaurant.menu.entities.MenuCatalogue;
import com.qr_restaurant.menu.use_cases.queries.GetCategoriesQuery;
import com.qr_restaurant.menu.use_cases.queries.GetMenuCataloguesQuery;
import com.qr_restaurant.menu.use_cases.queries.GetMenuItemsQuery;
import com.qr_restaurant.menu.use_cases.queries.dtos.MenuItemView;
import com.qr_restaurant.menu.vo.CategoryId;
import com.qr_restaurant.menu.vo.MenuCatalogueId;
import com.qr_restaurant.menu.web.dtos.responses.GetCategoriesResponseDto;
import com.qr_restaurant.menu.web.dtos.responses.GetMenuCataloguesResponseDto;
import com.qr_restaurant.menu.web.dtos.responses.GetMenuItemsResponseDto;
import com.qr_restaurant.menu.web.dtos.responses.common.CategoryDto;
import com.qr_restaurant.menu.web.dtos.responses.common.MenuCatalogueDto;
import com.qr_restaurant.menu.web.dtos.responses.common.MenuItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {

    private final GetMenuCataloguesQuery getMenuCataloguesQuery;
    private final GetCategoriesQuery getCategoriesQuery;
    private final GetMenuItemsQuery getMenuItemsQuery;

    private final Converter<MenuCatalogue, MenuCatalogueDto> menuCatalogueDtoConverter;
    private final Converter<Category, CategoryDto> categoryDtoConverter;
    private final Converter<MenuItemView, MenuItemDto> menuItemDtoConverter;

    @GetMapping("/catalogues")
    public ResponseEntity<GetMenuCataloguesResponseDto> getCatalogues() {
        var body = getMenuCataloguesQuery.query().stream().map(menuCatalogueDtoConverter::convert).toList();
        return ResponseEntity.ok(new GetMenuCataloguesResponseDto(body));
    }

    @GetMapping("/categories")
    public ResponseEntity<GetCategoriesResponseDto> getCategories() {
        var body = getCategoriesQuery.query().stream().map(categoryDtoConverter::convert).toList();
        return ResponseEntity.ok(new GetCategoriesResponseDto(body));
    }

    @GetMapping("/items")
    public ResponseEntity<GetMenuItemsResponseDto> getItems(
            @RequestParam(required = false) String catalogueId,
            @RequestParam(required = false) String categoryId) {

        var items = getMenuItemsQuery.query(
                catalogueId == null ? null : new MenuCatalogueId(catalogueId),
                categoryId == null ? null : new CategoryId(categoryId)
        );

        var body = items.stream().map(menuItemDtoConverter::convert).toList();
        return ResponseEntity.ok(new GetMenuItemsResponseDto(body));
    }
}
