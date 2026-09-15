package com.qr_restaurant.order.entities;

import com.qr_restaurant.common.Domain;
import com.qr_restaurant.menu.vo.MenuItemId;
import com.qr_restaurant.order.vo.OrderId;
import com.qr_restaurant.order.vo.OrderItemId;
import lombok.Getter;

@Getter
public class OrderItem extends Domain<OrderItemId> {
    private OrderId orderId;
    private MenuItemId menuItemId;
    private int quantity;
    private double price;

    public OrderItem(OrderItemId id, OrderId orderId, MenuItemId menuItemId, int quantity, double price) {
        super(id);
        this.orderId = orderId;
        this.menuItemId = menuItemId;
        this.quantity = quantity;
        this.price = price;
    }
}
