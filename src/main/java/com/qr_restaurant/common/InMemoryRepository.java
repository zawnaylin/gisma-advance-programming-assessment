package com.qr_restaurant.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public abstract class InMemoryRepository<ID, T extends Domain<ID>> {

    protected final Map<ID, T> store = new ConcurrentHashMap<>();

    public T save(T entity) {
        store.put(entity.getId(), entity);
        return entity;
    }

    public Optional<T> findById(ID id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<T> findAll() {
        return new ArrayList<>(store.values());
    }
}
