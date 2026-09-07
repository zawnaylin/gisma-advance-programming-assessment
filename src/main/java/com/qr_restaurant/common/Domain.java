package com.qr_restaurant.common;

import lombok.Getter;

public abstract class Domain<T> {
    @Getter
    private T id;

    public Domain() {
    }

    public Domain(T id) {
        this.id = id;
    }
}
