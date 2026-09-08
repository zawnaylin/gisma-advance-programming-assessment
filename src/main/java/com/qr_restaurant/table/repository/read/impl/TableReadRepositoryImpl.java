package com.qr_restaurant.table.repository.read.impl;

import com.qr_restaurant.table.application.entities.Table;
import com.qr_restaurant.table.repository.read.TableReadRepository;
import com.qr_restaurant.table.application.vo.TableId;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
class TableReadRepositoryImpl implements TableReadRepository {
    @Override
    public List<Table> getAll() {
        return List.of();
    }

    @Override
    public Table getById(TableId id) {
        return null;
    }
}
