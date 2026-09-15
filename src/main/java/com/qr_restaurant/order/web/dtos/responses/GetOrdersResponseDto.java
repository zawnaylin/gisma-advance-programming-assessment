package com.qr_restaurant.order.web.dtos.responses;

import com.qr_restaurant.order.web.dtos.responses.common.OrderDto;

import java.util.List;

public record GetOrdersResponseDto(List<OrderDto> orders) {
}
