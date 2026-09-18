package com.qr_restaurant.order.use_cases.commands.impl;

import com.qr_restaurant.order.repository.OrderRepository;
import com.qr_restaurant.order.repository.read.OrderReadRepository;
import com.qr_restaurant.order.use_cases.commands.MarkOrderReadyCommand;
import com.qr_restaurant.order.vo.OrderId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional
class MarkOrderReadyCommandImpl implements MarkOrderReadyCommand {

    private final OrderRepository orderRepository;
    private final OrderReadRepository orderReadRepository;

    MarkOrderReadyCommandImpl(OrderRepository orderRepository, OrderReadRepository orderReadRepository) {
        this.orderRepository = orderRepository;
        this.orderReadRepository = orderReadRepository;
    }

    @Override
    public void execute(OrderId id) {
        var order = orderReadRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No such order: " + id.value()));

        order.markReady();
        orderRepository.save(order);
    }
}
