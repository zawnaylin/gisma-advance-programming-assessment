package com.qr_restaurant.menu.web.dtos.responses;

import com.qr_restaurant.menu.web.dtos.responses.common.MenuCatalogueDto;

import java.util.List;

public record GetMenuCataloguesResponseDto(List<MenuCatalogueDto> catalogues) {
}
