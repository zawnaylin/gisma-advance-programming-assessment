package com.qr_restaurant.order.repository.impl;

import com.qr_restaurant.order.entities.Order;
import com.qr_restaurant.order.repository.OrderRepository;
import com.qr_restaurant.order.repository.read.OrderReadRepository;
import com.qr_restaurant.order.vo.OrderId;
import org.springframework.data.jpa.repository.JpaRepository;

interface JpaOrderRepository extends JpaRepository<Order, OrderId>, OrderRepository, OrderReadRepository {
}
