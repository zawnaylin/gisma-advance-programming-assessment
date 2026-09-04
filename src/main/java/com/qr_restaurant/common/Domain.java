package com.qr_restaurant.common;

import lombok.Getter;

public abstract class Domain<T> {
    @Getter
    private T id;
}
