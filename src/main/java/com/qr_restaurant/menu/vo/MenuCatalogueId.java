package com.qr_restaurant.menu.vo;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public record MenuCatalogueId(String value) implements Serializable {
}
