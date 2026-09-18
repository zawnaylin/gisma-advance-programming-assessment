package com.qr_restaurant.order.entities;

import com.qr_restaurant.common.Domain;
import com.qr_restaurant.menu.vo.MenuItemId;
import com.qr_restaurant.order.vo.OrderId;
import com.qr_restaurant.order.vo.OrderItemId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.Getter;

/**
 * One line of an order: which menu item, how many, and the price when it was ordered.
 * The price is copied so later menu changes don't rewrite past orders.
 */
@Getter
@Entity
@jakarta.persistence.Table(name = "order_items")
public class OrderItem extends Domain<OrderItemId> {
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "order_id"))
    private OrderId orderId;
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "menu_item_id"))
    private MenuItemId menuItemId;
    private int quantity;
    private double price;

    protected OrderItem() {
    }

    public OrderItem(OrderItemId id, OrderId orderId, MenuItemId menuItemId, int quantity, double price) {
        super(id);
        this.orderId = orderId;
        this.menuItemId = menuItemId;
        this.quantity = quantity;
        this.price = price;
    }
}
