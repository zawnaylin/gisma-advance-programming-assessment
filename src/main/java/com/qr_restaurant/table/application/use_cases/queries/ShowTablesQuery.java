package com.qr_restaurant.table.application.use_cases.queries;

import com.qr_restaurant.table.application.entities.Table;

import java.util.List;

public interface ShowTablesQuery {
    List<Table> query();
}
