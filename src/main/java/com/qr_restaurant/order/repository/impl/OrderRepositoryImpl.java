package com.qr_restaurant.order.repository.impl;

import com.qr_restaurant.common.InMemoryRepository;
import com.qr_restaurant.order.entities.Order;
import com.qr_restaurant.order.repository.OrderRepository;
import com.qr_restaurant.order.repository.read.OrderReadRepository;
import com.qr_restaurant.order.vo.OrderId;
import org.springframework.stereotype.Repository;

@Repository
class OrderRepositoryImpl extends InMemoryRepository<OrderId, Order> implements OrderRepository, OrderReadRepository {
}
