package com.qr_restaurant.table.application.use_cases.queries.impl;

import com.qr_restaurant.table.application.entities.Table;
import com.qr_restaurant.table.application.use_cases.queries.ShowTablesQuery;
import com.qr_restaurant.table.repository.read.TableReadRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
class ShowTableImpl implements ShowTablesQuery {

    private final TableReadRepository tableReadRepository;

    ShowTableImpl(TableReadRepository tableReadRepository) {
        this.tableReadRepository = tableReadRepository;
    }

    @Override
    public List<Table> query() {
        return tableReadRepository.findAll().stream()
                .sorted(Comparator.comparing(table -> table.getId().value()))
                .toList();
    }
}
