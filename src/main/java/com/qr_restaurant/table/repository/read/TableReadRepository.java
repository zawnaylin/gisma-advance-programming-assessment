package com.qr_restaurant.table.repository.read;

import com.qr_restaurant.table.application.entities.Table;
import com.qr_restaurant.table.application.vo.TableId;

import java.util.List;
import java.util.Optional;

public interface TableReadRepository {

    List<Table> findAll();

    Optional<Table> findById(TableId id);
}
