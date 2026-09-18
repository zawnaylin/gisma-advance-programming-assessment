package com.qr_restaurant.table.application.use_cases.queries.impl;

import com.qr_restaurant.table.application.entities.Table;
import com.qr_restaurant.table.application.use_cases.queries.GetTableQuery;
import com.qr_restaurant.table.application.vo.TableId;
import com.qr_restaurant.table.repository.read.TableReadRepository;
import org.springframework.stereotype.Service;

@Service
class GetTableQueryImpl implements GetTableQuery {

    private final TableReadRepository tableReadRepository;

    GetTableQueryImpl(TableReadRepository tableReadRepository) {
        this.tableReadRepository = tableReadRepository;
    }

    @Override
    public Table query(TableId id) {
        return tableReadRepository.findById(id).orElse(null);
    }
}
