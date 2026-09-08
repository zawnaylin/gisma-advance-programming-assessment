package com.qr_restaurant.table.web.dtos.responses;

import com.qr_restaurant.table.web.dtos.responses.common.TableDto;

import java.util.List;

public record GetTablesResponseDto(
        List<TableDto> tables
) {
}
