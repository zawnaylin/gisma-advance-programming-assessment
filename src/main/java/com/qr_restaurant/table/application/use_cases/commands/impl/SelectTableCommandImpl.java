package com.qr_restaurant.table.application.use_cases.commands.impl;

import com.qr_restaurant.table.application.use_cases.commands.SelectTableCommand;
import com.qr_restaurant.table.application.vo.DiningSessionId;
import com.qr_restaurant.table.application.vo.TableId;
import com.qr_restaurant.table.repository.TableRepository;
import com.qr_restaurant.table.repository.read.TableReadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Transactional
class SelectTableCommandImpl implements SelectTableCommand {

    private final TableRepository tableRepository;
    private final TableReadRepository tableReadRepository;

    SelectTableCommandImpl(TableRepository tableRepository, TableReadRepository tableReadRepository) {
        this.tableRepository = tableRepository;
        this.tableReadRepository = tableReadRepository;
    }

    @Override
    public void execute(TableId id) {
        var table = tableReadRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No such table: " + id.value()));

        table.seat(new DiningSessionId(UUID.randomUUID().toString()), 1);
        // The new session is saved through the table (cascade).
        tableRepository.save(table);
    }
}
