package com.qr_restaurant.table.repository.read;

import com.qr_restaurant.table.application.entities.Table;
import com.qr_restaurant.table.application.vo.TableId;

import java.util.List;

public interface TableReadRepository {

    List<Table> getAll();

    Table getById(TableId id);
}
