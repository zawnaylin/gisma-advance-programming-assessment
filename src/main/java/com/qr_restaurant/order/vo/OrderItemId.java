package com.qr_restaurant.order.vo;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public record OrderItemId(String value) implements Serializable {
}
