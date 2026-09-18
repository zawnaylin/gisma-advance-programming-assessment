package com.qr_restaurant.table.application.use_cases.commands.impl;

import com.qr_restaurant.table.application.use_cases.commands.MarkTableCleanedCommand;
import com.qr_restaurant.table.application.vo.TableId;
import com.qr_restaurant.table.repository.TableRepository;
import com.qr_restaurant.table.repository.read.TableReadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional
class MarkTableCleanedCommandImpl implements MarkTableCleanedCommand {

    private final TableRepository tableRepository;
    private final TableReadRepository tableReadRepository;

    MarkTableCleanedCommandImpl(TableRepository tableRepository, TableReadRepository tableReadRepository) {
        this.tableRepository = tableRepository;
        this.tableReadRepository = tableReadRepository;
    }

    @Override
    public void execute(TableId id) {
        var table = tableReadRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No such table: " + id.value()));

        // Only a table whose dining session has ended (WAITING_FOR_CLEANING) can be cleaned.
        table.markCleaned();
        tableRepository.save(table);
    }
}
