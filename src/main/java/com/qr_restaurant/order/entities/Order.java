package com.qr_restaurant.order.entities;

import com.qr_restaurant.common.Domain;
import com.qr_restaurant.common.StateMachine;
import com.qr_restaurant.order.enums.OrderStatus;
import com.qr_restaurant.order.vo.OrderId;
import com.qr_restaurant.table.application.vo.DiningSessionId;
import lombok.Getter;

import java.util.List;

@Getter
public class Order extends Domain<OrderId> {

    private static final StateMachine<OrderStatus> STATUS_TRANSITIONS = StateMachine.builder(OrderStatus.class)
            .allow(OrderStatus.PENDING, OrderStatus.CONFIRMED, OrderStatus.CANCELLED)
            .allow(OrderStatus.CONFIRMED, OrderStatus.READY, OrderStatus.CANCELLED)
            .allow(OrderStatus.READY, OrderStatus.SERVED)
            .build();

    private DiningSessionId diningSessionId;
    private List<OrderItem> items;
    private OrderStatus status;
    private String cancelledBy;
    private String cancellationReason;

    public Order() {
    }

    public Order(OrderId id) {
        super(id);
        this.status = OrderStatus.PENDING;
    }

    public Order(OrderId id, DiningSessionId diningSessionId, List<OrderItem> items) {
        super(id);
        this.diningSessionId = diningSessionId;
        this.items = items;
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