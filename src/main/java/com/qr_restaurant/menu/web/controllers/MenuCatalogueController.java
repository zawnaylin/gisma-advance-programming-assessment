package com.qr_restaurant.menu.web.controllers;

import com.qr_restaurant.menu.entities.MenuCatalogue;
import com.qr_restaurant.menu.use_cases.commands.ManageMenuCatalogueCommand;
import com.qr_restaurant.menu.use_cases.commands.dtos.MenuCatalogueDetails;
import com.qr_restaurant.menu.use_cases.queries.GetMenuCatalogueQuery;
import com.qr_restaurant.menu.use_cases.queries.GetMenuCataloguesQuery;
import com.qr_restaurant.menu.vo.MenuCatalogueId;
import com.qr_restaurant.menu.web.dtos.requests.MenuCatalogueRequestDto;
import com.qr_restaurant.menu.web.dtos.responses.GetMenuCataloguesResponseDto;
import com.qr_restaurant.menu.web.dtos.responses.MenuCatalogueResponseDto;
import com.qr_restaurant.menu.web.dtos.responses.common.MenuCatalogueDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * REST API for menu catalogues. See docs/rest-api.md.
 */
@RestController
@RequestMapping("/api/menu/catalogues")
@RequiredArgsConstructor
public class MenuCatalogueController {

    private final GetMenuCataloguesQuery getMenuCataloguesQuery;
    private final GetMenuCatalogueQuery getMenuCatalogueQuery;
    private final ManageMenuCatalogueCommand manageMenuCatalogueCommand;

    private final Converter<MenuCatalogue, MenuCatalogueDto> menuCatalogueDtoConverter;

    /**
     * @return all menu catalogues
     */
    @GetMapping
    public ResponseEntity<GetMenuCataloguesResponseDto> getCatalogues() {
        var body = getMenuCataloguesQuery.query().stream().map(menuCatalogueDtoConverter::convert).toList();
        return ResponseEntity.ok(new GetMenuCataloguesResponseDto(body));
    }

    /**
     * @return the catalogue, or 404 if there is none with that id
     */
    @GetMapping("/{id}")
    public ResponseEntity<MenuCatalogueResponseDto> getCatalogue(@PathVariable String id) {
        var catalogue = getMenuCatalogueQuery.query(new MenuCatalogueId(id));
        if (catalogue == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new MenuCatalogueResponseDto(menuCatalogueDtoConverter.convert(catalogue)));
    }

    /**
     * Creates a catalogue; the body's id is optional and generated when absent.
     *
     * @return 201 with a Location header, or 400 for an invalid body and 409 if the id is taken
     */
    @PostMapping
    public ResponseEntity<MenuCatalogueResponseDto> createCatalogue(@Valid @RequestBody MenuCatalogueRequestDto request) {
        var id = manageMenuCatalogueCommand.create(
                request.id() == null ? null : new MenuCatalogueId(request.id()),
                new MenuCatalogueDetails(request.name(), request.description()));

        var body = menuCatalogueDtoConverter.convert(getMenuCatalogueQuery.query(id));
        return ResponseEntity.created(URI.create("/api/menu/catalogues/" + id.value()))
                .body(new MenuCatalogueResponseDto(body));
    }

    /**
     * Replaces a catalogue's details. Never creates one.
     *
     * @return 200 with the updated resource, 400 for an invalid body, or 404 if it doesn't exist
     */
    @PutMapping("/{id}")
    public ResponseEntity<MenuCatalogueResponseDto> updateCatalogue(@PathVariable String id,
                                                                    @Valid @RequestBody MenuCatalogueRequestDto request) {
        var catalogueId = new MenuCatalogueId(id);
        manageMenuCatalogueCommand.update(catalogueId, new MenuCatalogueDetails(request.name(), request.description()));

        var body = menuCatalogueDtoConverter.convert(getMenuCatalogueQuery.query(catalogueId));
        return ResponseEntity.ok(new MenuCatalogueResponseDto(body));
    }

    /**
     * Deletes a catalogue.
     *
     * @return 204, 404 if it doesn't exist, or 409 if menu items still belong to it
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCatalogue(@PathVariable String id) {
        manageMenuCatalogueCommand.delete(new MenuCatalogueId(id));
        return ResponseEntity.noContent().build();
    }
}
