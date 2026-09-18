package com.qr_restaurant.order.use_cases.queries.impl;

import com.qr_restaurant.order.entities.Order;
import com.qr_restaurant.order.repository.read.OrderReadRepository;
import com.qr_restaurant.order.use_cases.queries.ViewSessionOrdersQuery;
import com.qr_restaurant.table.application.vo.DiningSessionId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class ViewSessionOrdersQueryImpl implements ViewSessionOrdersQuery {

    private final OrderReadRepository orderReadRepository;

    ViewSessionOrdersQueryImpl(OrderReadRepository orderReadRepository) {
        this.orderReadRepository = orderReadRepository;
    }

    @Override
    public List<Order> query(DiningSessionId diningSessionId) {
        return orderReadRepository.findByDiningSessionId(diningSessionId);
    }
}
