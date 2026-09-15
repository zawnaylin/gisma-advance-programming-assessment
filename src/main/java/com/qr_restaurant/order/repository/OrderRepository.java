package com.qr_restaurant.order.repository;

import com.qr_restaurant.order.entities.Order;

public interface OrderRepository {

    Order save(Order order);
}
