package com.qr_restaurant.table;

import com.qr_restaurant.table.application.entities.Table;
import com.qr_restaurant.table.application.vo.TableId;
import com.qr_restaurant.table.repository.TableRepository;
import com.qr_restaurant.table.repository.read.TableReadRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
class TableDataSeeder {

    private final TableRepository tableRepository;
    private final TableReadRepository tableReadRepository;

    TableDataSeeder(TableRepository tableRepository, TableReadRepository tableReadRepository) {
        this.tableRepository = tableRepository;
        this.tableReadRepository = tableReadRepository;
    }

    @PostConstruct
    void seed() {
        // Data persists across restarts; re-saving would reset table statuses.
        if (!tableReadRepository.findAll().isEmpty()) {
            return;
        }

        tableRepository.save(new Table(new TableId("T1"), 2));
        tableRepository.save(new Table(new TableId("T2"), 4));
        tableRepository.save(new Table(new TableId("T3"), 4));
        tableRepository.save(new Table(new TableId("T4"), 6));
    }
}
