package com.qr_restaurant.order.use_cases.commands.impl;

import com.qr_restaurant.order.repository.OrderRepository;
import com.qr_restaurant.order.repository.read.OrderReadRepository;
import com.qr_restaurant.order.use_cases.commands.CancelOrderCommand;
import com.qr_restaurant.order.use_cases.commands.dtos.CancelOrderDto;
import com.qr_restaurant.order.vo.OrderId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional
class CancelOrderCommandImpl implements CancelOrderCommand {

    private final OrderRepository orderRepository;
    private final OrderReadRepository orderReadRepository;

    CancelOrderCommandImpl(OrderRepository orderRepository, OrderReadRepository orderReadRepository) {
        this.orderRepository = orderRepository;
        this.orderReadRepository = orderReadRepository;
    }

    @Override
    public void execute(OrderId id, CancelOrderDto request) {
        var order = orderReadRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No such order: " + id.value()));

        order.cancel(request.cancelledBy(), request.reason());
        orderRepository.save(order);
    }
}
