package com.qr_restaurant.order.use_cases.commands.impl;

import com.qr_restaurant.menu.use_cases.queries.GetMenuItemQuery;
import com.qr_restaurant.order.entities.Order;
import com.qr_restaurant.order.entities.OrderItem;
import com.qr_restaurant.order.repository.OrderRepository;
import com.qr_restaurant.order.use_cases.commands.PlaceOrderCommand;
import com.qr_restaurant.order.use_cases.commands.dtos.PlaceOrderDto;
import com.qr_restaurant.order.vo.OrderId;
import com.qr_restaurant.order.vo.OrderItemId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
class PlaceOrderCommandImpl implements PlaceOrderCommand {

    private final OrderRepository orderRepository;
    private final GetMenuItemQuery getMenuItemQuery;

    PlaceOrderCommandImpl(OrderRepository orderRepository, GetMenuItemQuery getMenuItemQuery) {
        this.orderRepository = orderRepository;
        this.getMenuItemQuery = getMenuItemQuery;
    }

    @Override
    public OrderId execute(PlaceOrderDto request) {
        var orderId = new OrderId(UUID.randomUUID().toString());

        List<OrderItem> items = request.items().stream()
                .map(line -> {
                    var menuItem = getMenuItemQuery.query(line.menuItemId());
                    if (menuItem == null) {
                        throw new IllegalArgumentException("No such menu item: " + line.menuItemId().value());
                    }

                    return new OrderItem(
                            new OrderItemId(UUID.randomUUID().toString()),
                            orderId,
                            line.menuItemId(),
                            line.quantity(),
                            menuItem.price()
                    );
                })
                .toList();

        var order = new Order(orderId, request.diningSessionId(), items);
        orderRepository.save(order);

        return orderId;
    }
}
