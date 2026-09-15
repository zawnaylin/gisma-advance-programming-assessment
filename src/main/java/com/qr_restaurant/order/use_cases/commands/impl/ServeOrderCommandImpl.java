package com.qr_restaurant.order.use_cases.commands.impl;

import com.qr_restaurant.order.repository.OrderRepository;
import com.qr_restaurant.order.repository.read.OrderReadRepository;
import com.qr_restaurant.order.use_cases.commands.ServeOrderCommand;
import com.qr_restaurant.order.vo.OrderId;
import org.springframework.stereotype.Service;

@Service
class ServeOrderCommandImpl implements ServeOrderCommand {

    private final OrderRepository orderRepository;
    private final OrderReadRepository orderReadRepository;

    ServeOrderCommandImpl(OrderRepository orderRepository, OrderReadRepository orderReadRepository) {
        this.orderRepository = orderRepository;
        this.orderReadRepository = orderReadRepository;
    }

    @Override
    public void execute(OrderId id) {
        var order = orderReadRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No such order: " + id.value()));

        order.serve();
        orderRepository.save(order);
    }
}
