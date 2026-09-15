package com.qr_restaurant.order.web.dtos.responses.common;

import java.util.List;

public record OrderDto(
        String id,
        String diningSessionId,
        String status,
        List<OrderItemDto> items,
        String cancelledBy,
        String cancellationReason
) {
}
