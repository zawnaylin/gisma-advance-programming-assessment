package com.qr_restaurant.order.use_cases.queries;

import com.qr_restaurant.order.entities.Order;
import com.qr_restaurant.order.enums.OrderStatus;

import java.util.List;

public interface ViewIncomingOrdersQuery {
    List<Order> query(OrderStatus statusFilter);
}
