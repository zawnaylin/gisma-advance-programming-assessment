package com.qr_restaurant.table.application.vo;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public record DiningSessionId(String value) implements Serializable {
}
