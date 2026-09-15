package com.qr_restaurant.order.use_cases.queries;

import com.qr_restaurant.order.entities.Order;
import com.qr_restaurant.order.vo.OrderId;

public interface TrackOrderStatusQuery {
    Order query(OrderId id);
}
