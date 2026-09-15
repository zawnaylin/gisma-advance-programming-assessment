package com.qr_restaurant.menu.web.converters;

import com.qr_restaurant.menu.entities.Category;
import com.qr_restaurant.menu.web.dtos.responses.common.CategoryDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CategoryDtoConverter implements Converter<Category, CategoryDto> {
    @Override
    public CategoryDto convert(Category source) {
        return new CategoryDto(source.getId().value(), source.getName(), source.getDescription());
    }
}
