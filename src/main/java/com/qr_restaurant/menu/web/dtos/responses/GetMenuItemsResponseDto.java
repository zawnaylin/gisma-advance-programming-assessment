package com.qr_restaurant.menu.web.dtos.responses;

import com.qr_restaurant.menu.web.dtos.responses.common.MenuItemDto;

import java.util.List;

public record GetMenuItemsResponseDto(List<MenuItemDto> items) {
}
