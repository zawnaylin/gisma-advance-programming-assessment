package com.qr_restaurant.table.application.use_cases.commands.impl;

import com.qr_restaurant.table.application.entities.Table;
import com.qr_restaurant.table.application.use_cases.commands.ManageTableCommand;
import com.qr_restaurant.table.application.vo.TableId;
import com.qr_restaurant.table.repository.TableRepository;
import com.qr_restaurant.table.repository.read.DiningSessionReadRepository;
import com.qr_restaurant.table.repository.read.TableReadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Transactional
class ManageTableCommandImpl implements ManageTableCommand {

    private final TableRepository tableRepository;
    private final TableReadRepository tableReadRepository;
    private final DiningSessionReadRepository diningSessionReadRepository;

    ManageTableCommandImpl(TableRepository tableRepository,
                           TableReadRepository tableReadRepository,
                           DiningSessionReadRepository diningSessionReadRepository) {
        this.tableRepository = tableRepository;
        this.tableReadRepository = tableReadRepository;
        this.diningSessionReadRepository = diningSessionReadRepository;
    }

    @Override
    public TableId create(TableId id, int capacity) {
        var tableId = id != null ? id : new TableId(UUID.randomUUID().toString());
        // save() would silently overwrite an existing table, including its current session.
        if (tableReadRepository.findById(tableId).isPresent()) {
            throw new IllegalStateException("Table already exists: " + tableId.value());
        }

        tableRepository.save(new Table(tableId, capacity));
        return tableId;
    }

    @Override
    public void updateCapacity(TableId id, int capacity) {
        var table = tableReadRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No such table: " + id.value()));

        table.updateCapacity(capacity);
        tableRepository.save(table);
    }

    @Override
    public void delete(TableId id) {
        var table = tableReadRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No such table: " + id.value()));

        if (table.getCurrentSession() != null) {
            throw new IllegalStateException("Table " + id.value() + " is in use (" + table.getStatus()
                    + "); end its dining session and clean it first");
        }
        // Past sessions reference the table, so removing it would lose that history.
        var sessions = diningSessionReadRepository.findByTableId(id).size();
        if (sessions > 0) {
            throw new IllegalStateException("Table " + id.value() + " still has " + sessions
                    + " dining session(s) in its history and cannot be deleted");
        }

        tableRepository.deleteById(id);
    }
}
