package com.qr_restaurant.order.repository.read;

import com.qr_restaurant.order.entities.Order;
import com.qr_restaurant.order.vo.OrderId;

import java.util.List;
import java.util.Optional;

public interface OrderReadRepository {

    List<Order> findAll();

    Optional<Order> findById(OrderId id);
}
