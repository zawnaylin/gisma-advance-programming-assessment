package com.qr_restaurant.order.use_cases.queries.impl;

import com.qr_restaurant.order.entities.Order;
import com.qr_restaurant.order.repository.read.OrderReadRepository;
import com.qr_restaurant.order.use_cases.queries.TrackOrderStatusQuery;
import com.qr_restaurant.order.vo.OrderId;
import org.springframework.stereotype.Service;

@Service
class TrackOrderStatusQueryImpl implements TrackOrderStatusQuery {

    private final OrderReadRepository orderReadRepository;

    TrackOrderStatusQueryImpl(OrderReadRepository orderReadRepository) {
        this.orderReadRepository = orderReadRepository;
    }

    @Override
    public Order query(OrderId id) {
        return orderReadRepository.findById(id).orElse(null);
    }
}
