package com.qr_restaurant.order.use_cases.queries.impl;

import com.qr_restaurant.order.entities.Order;
import com.qr_restaurant.order.enums.OrderStatus;
import com.qr_restaurant.order.repository.read.OrderReadRepository;
import com.qr_restaurant.order.use_cases.queries.ViewIncomingOrdersQuery;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class ViewIncomingOrdersQueryImpl implements ViewIncomingOrdersQuery {

    private final OrderReadRepository orderReadRepository;

    ViewIncomingOrdersQueryImpl(OrderReadRepository orderReadRepository) {
        this.orderReadRepository = orderReadRepository;
    }

    @Override
    public List<Order> query(OrderStatus statusFilter) {
        return orderReadRepository.findAll().stream()
                .filter(order -> statusFilter == null || order.getStatus() == statusFilter)
                .toList();
    }
}
