package com.qr_restaurant.menu.vo;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public record CategoryId(String value) implements Serializable {
}
