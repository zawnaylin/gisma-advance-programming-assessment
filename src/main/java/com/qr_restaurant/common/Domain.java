package com.qr_restaurant.common;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

/**
 * Base class for entities identified by a value-object id (e.g. {@code TableId}).
 *
 * @param <T> the id type, stored as the entity's primary key
 */
@MappedSuperclass
public abstract class Domain<T> {
    @Getter
    @EmbeddedId
    @AttributeOverride(name = "value", column = @Column(name = "id"))
    private T id;

    public Domain() {
    }

    public Domain(T id) {
        this.id = id;
    }
}
