package com.qr_restaurant.table;

import com.qr_restaurant.table.application.entities.Table;
import com.qr_restaurant.table.application.vo.TableId;
import com.qr_restaurant.table.repository.TableRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
class TableDataSeeder {

    private final TableRepository tableRepository;

    TableDataSeeder(TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    @PostConstruct
    void seed() {
        tableRepository.save(new Table(new TableId("T1"), 2));
        tableRepository.save(new Table(new TableId("T2"), 4));
        tableRepository.save(new Table(new TableId("T3"), 4));
        tableRepository.save(new Table(new TableId("T4"), 6));
    }
}
