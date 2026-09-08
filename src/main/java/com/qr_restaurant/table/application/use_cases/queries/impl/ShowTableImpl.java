package com.qr_restaurant.table.application.use_cases.queries.impl;

import com.qr_restaurant.table.application.entities.Table;
import com.qr_restaurant.table.application.use_cases.queries.ShowTablesQuery;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class ShowTableImpl implements ShowTablesQuery {

    @Override
    public List<Table> query() {
        return List.of();
    }
}
