package com.qr_restaurant.menu.web.converters;

import com.qr_restaurant.menu.use_cases.queries.dtos.MenuItemView;
import com.qr_restaurant.menu.web.dtos.responses.common.MenuItemDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class MenuItemDtoConverter implements Converter<MenuItemView, MenuItemDto> {
    @Override
    public MenuItemDto convert(MenuItemView source) {
        return new MenuItemDto(
                source.id().value(),
                source.name(),
                source.description(),
                source.price(),
                source.catalogueId().value(),
                source.categoryId().value()
        );
    }
}
