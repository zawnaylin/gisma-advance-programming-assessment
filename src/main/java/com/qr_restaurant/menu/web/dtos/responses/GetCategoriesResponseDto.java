package com.qr_restaurant.menu.web.dtos.responses;

import com.qr_restaurant.menu.web.dtos.responses.common.CategoryDto;

import java.util.List;

public record GetCategoriesResponseDto(List<CategoryDto> categories) {
}
