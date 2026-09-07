package com.qr_restaurant.order.entities;

import com.qr_restaurant.common.Domain;
import com.qr_restaurant.common.StateMachine;
import com.qr_restaurant.order.enums.OrderStatus;
import com.qr_restaurant.order.vo.OrderId;
import lombok.Getter;

public class Order extends Domain<OrderId> {

    private static final StateMachine<OrderStatus> STATUS_TRANSITIONS = StateMachine.builder(OrderStatus.class)
            .allow(OrderStatus.PENDING, OrderStatus.CONFIRMED, OrderStatus.CANCELLED)
            .allow(OrderStatus.CONFIRMED, OrderStatus.READY, OrderStatus.CANCELLED)
            .allow(OrderStatus.READY, OrderStatus.SERVED)
            .build();

    @Getter
    private OrderStatus status;

    @Getter
    private String cancelledBy;

    @Getter
    private String cancellationReason;

    public Order() {
    }

    public Order(OrderId id) {
        super(id);
        this.status = OrderStatus.PENDING;
    }

    public void confirmOrder() {
        transitionTo(OrderStatus.CONFIRMED);
    }

    public void markReady() {
        transitionTo(OrderStatus.READY);
    }

    public void serve() {
        transitionTo(OrderStatus.SERVED);
    }

    public void cancel(String cancelledBy, String cancellationReason) {
        transitionTo(OrderStatus.CANCELLED);
        this.cancelledBy = cancelledBy;
        this.cancellationReason = cancellationReason;
    }

    private void transitionTo(OrderStatus target) {
        STATUS_TRANSITIONS.validate(this.status, target);
        this.status = target;
    }
}