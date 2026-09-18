package com.qr_restaurant.order.entities;

import com.qr_restaurant.common.Domain;
import com.qr_restaurant.common.StateMachine;
import com.qr_restaurant.order.enums.OrderStatus;
import com.qr_restaurant.order.events.OrderStatusChanged;
import com.qr_restaurant.order.vo.OrderId;
import com.qr_restaurant.table.application.vo.DiningSessionId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Entity
@jakarta.persistence.Table(name = "orders")
public class Order extends Domain<OrderId> {

    private static final StateMachine<OrderStatus> STATUS_TRANSITIONS = StateMachine.builder(OrderStatus.class)
            .allow(OrderStatus.PENDING, OrderStatus.CONFIRMED, OrderStatus.CANCELLED)
            .allow(OrderStatus.CONFIRMED, OrderStatus.READY, OrderStatus.CANCELLED)
            .allow(OrderStatus.READY, OrderStatus.SERVED)
            .build();

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "dining_session_id"))
    private DiningSessionId diningSessionId;

    // OrderItem owns the order_id column itself, so the association only reads it.
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private List<OrderItem> items;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private String cancelledBy;
    private String cancellationReason;
    private Instant placedAt;

    // Published by Spring Data on orderRepository.save(order); see Table for the same pattern.
    @Transient
    @Getter(AccessLevel.NONE)
    private final List<Object> domainEvents = new ArrayList<>();

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
        this.placedAt = Instant.now();
        this.domainEvents.add(new OrderStatusChanged(id, OrderStatus.PENDING));
    }

    /**
     * The kitchen accepts the order: PENDING to CONFIRMED.
     *
     * @throws IllegalStateException if the order is not pending
     */
    public void confirmOrder() {
        transitionTo(OrderStatus.CONFIRMED);
    }

    /**
     * Cooking is finished: CONFIRMED to READY.
     *
     * @throws IllegalStateException if the order is not confirmed
     */
    public void markReady() {
        transitionTo(OrderStatus.READY);
    }

    /**
     * The order reached the table: READY to SERVED, which is final.
     *
     * @throws IllegalStateException if the order is not ready
     */
    public void serve() {
        transitionTo(OrderStatus.SERVED);
    }

    /**
     * Cancels the order and records who did it and why.
     *
     * @throws IllegalStateException if the order is already served or cancelled
     */
    public void cancel(String cancelledBy, String cancellationReason) {
        transitionTo(OrderStatus.CANCELLED);
        this.cancelledBy = cancelledBy;
        this.cancellationReason = cancellationReason;
    }

    /**
     * @return whether the order may still be cancelled, i.e. it is pending or confirmed
     */
    public boolean canCancel() {
        return STATUS_TRANSITIONS.canTransition(this.status, OrderStatus.CANCELLED);
    }

    /**
     * @return whether the order has been served or cancelled, so nothing more will happen to it
     */
    public boolean isFinished() {
        return this.status == OrderStatus.SERVED || this.status == OrderStatus.CANCELLED;
    }

    private void transitionTo(OrderStatus target) {
        STATUS_TRANSITIONS.validate(this.status, target);
        this.status = target;
        this.domainEvents.add(new OrderStatusChanged(getId(), target));
    }

    @DomainEvents
    Collection<Object> domainEvents() {
        return List.copyOf(domainEvents);
    }

    @AfterDomainEventPublication
    void clearDomainEvents() {
        domainEvents.clear();
    }
}