package com.qr_restaurant.menu.web.controllers;

import com.qr_restaurant.menu.use_cases.commands.ManageMenuItemCommand;
import com.qr_restaurant.menu.use_cases.commands.dtos.MenuItemDetails;
import com.qr_restaurant.menu.use_cases.queries.GetMenuItemQuery;
import com.qr_restaurant.menu.use_cases.queries.GetMenuItemsQuery;
import com.qr_restaurant.menu.use_cases.queries.dtos.MenuItemView;
import com.qr_restaurant.menu.vo.CategoryId;
import com.qr_restaurant.menu.vo.MenuCatalogueId;
import com.qr_restaurant.menu.vo.MenuItemId;
import com.qr_restaurant.menu.web.dtos.requests.MenuItemRequestDto;
import com.qr_restaurant.menu.web.dtos.responses.GetMenuItemsResponseDto;
import com.qr_restaurant.menu.web.dtos.responses.MenuItemResponseDto;
import com.qr_restaurant.menu.web.dtos.responses.common.MenuItemDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * REST API for menu items. See docs/rest-api.md.
 */
@RestController
@RequestMapping("/api/menu/items")
@RequiredArgsConstructor
public class MenuItemController {

    private final GetMenuItemsQuery getMenuItemsQuery;
    private final GetMenuItemQuery getMenuItemQuery;
    private final ManageMenuItemCommand manageMenuItemCommand;

    private final Converter<MenuItemView, MenuItemDto> menuItemDtoConverter;

    /**
     * @param catalogueId only items in this catalogue, or absent for any
     * @param categoryId  only items in this category, or absent for any
     * @return the matching menu items
     */
    @GetMapping
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

    /**
     * @return the menu item, or 404 if there is none with that id
     */
    @GetMapping("/{id}")
    public ResponseEntity<MenuItemResponseDto> getItem(@PathVariable String id) {
        var item = getMenuItemQuery.query(new MenuItemId(id));
        if (item == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new MenuItemResponseDto(menuItemDtoConverter.convert(item)));
    }

    /**
     * Creates a menu item; the body's id is optional and generated when absent.
     *
     * @return 201 with a Location header, 400 for an invalid body or an unknown
     *         catalogue/category, or 409 if the id is taken
     */
    @PostMapping
    public ResponseEntity<MenuItemResponseDto> createItem(@Valid @RequestBody MenuItemRequestDto request) {
        var id = manageMenuItemCommand.create(
                request.id() == null ? null : new MenuItemId(request.id()),
                toDetails(request));

        var body = menuItemDtoConverter.convert(getMenuItemQuery.query(id));
        return ResponseEntity.created(URI.create("/api/menu/items/" + id.value()))
                .body(new MenuItemResponseDto(body));
    }

    /**
     * Replaces a menu item's details. Never creates one.
     *
     * @return 200 with the updated item, 400 for an invalid body or an unknown
     *         catalogue/category, or 404 if the item doesn't exist
     */
    @PutMapping("/{id}")
    public ResponseEntity<MenuItemResponseDto> updateItem(@PathVariable String id,
                                                          @Valid @RequestBody MenuItemRequestDto request) {
        var itemId = new MenuItemId(id);
        manageMenuItemCommand.update(itemId, toDetails(request));

        var body = menuItemDtoConverter.convert(getMenuItemQuery.query(itemId));
        return ResponseEntity.ok(new MenuItemResponseDto(body));
    }

    /**
     * Deletes a menu item. Past orders keep its id and the price they were placed at.
     *
     * @return 204, or 404 if it doesn't exist
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable String id) {
        manageMenuItemCommand.delete(new MenuItemId(id));
        return ResponseEntity.noContent().build();
    }

    private static MenuItemDetails toDetails(MenuItemRequestDto request) {
        return new MenuItemDetails(
                request.name(),
                request.description(),
                request.price(),
                new MenuCatalogueId(request.catalogueId()),
                new CategoryId(request.categoryId())
        );
    }
}
