package com.qr_restaurant.order.web.converters;

import com.qr_restaurant.order.entities.Order;
import com.qr_restaurant.order.entities.OrderItem;
import com.qr_restaurant.order.web.dtos.responses.common.OrderDto;
import com.qr_restaurant.order.web.dtos.responses.common.OrderItemDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderDtoConverter implements Converter<Order, OrderDto> {
    @Override
    public OrderDto convert(Order source) {
        var items = source.getItems() == null ? List.<OrderItem>of() : source.getItems();

        var itemDtos = items.stream()
                .map(item -> new OrderItemDto(
                        item.getId().value(),
                        item.getMenuItemId().value(),
                        item.getQuantity(),
                        item.getPrice()
                ))
                .toList();

        return new OrderDto(
                source.getId().value(),
                source.getDiningSessionId() == null ? null : source.getDiningSessionId().value(),
                source.getStatus().name(),
                itemDtos,
                source.getCancelledBy(),
                source.getCancellationReason()
        );
    }
}
