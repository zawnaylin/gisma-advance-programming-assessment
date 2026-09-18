package com.qr_restaurant.order.events;

import com.qr_restaurant.order.enums.OrderStatus;
import com.qr_restaurant.order.vo.OrderId;

/**
 * Registered by {@code Order} when it is placed (PENDING) and on every status transition,
 * and published by Spring Data when the order is saved.
 *
 * @param orderId the order that changed
 * @param status  the status it moved to
 */
public record OrderStatusChanged(OrderId orderId, OrderStatus status) {
}
