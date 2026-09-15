package com.qr_restaurant.menu.web.converters;

import com.qr_restaurant.menu.entities.MenuCatalogue;
import com.qr_restaurant.menu.web.dtos.responses.common.MenuCatalogueDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class MenuCatalogueDtoConverter implements Converter<MenuCatalogue, MenuCatalogueDto> {
    @Override
    public MenuCatalogueDto convert(MenuCatalogue source) {
        return new MenuCatalogueDto(source.getId().value(), source.getName(), source.getDescription());
    }
}
