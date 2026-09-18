package com.qr_restaurant.table.application.use_cases.commands.impl;

import com.qr_restaurant.table.application.entities.Table;
import com.qr_restaurant.table.application.use_cases.commands.EndDiningSessionCommand;
import com.qr_restaurant.table.application.vo.DiningSessionId;
import com.qr_restaurant.table.application.vo.TableId;
import com.qr_restaurant.table.repository.TableRepository;
import com.qr_restaurant.table.repository.read.DiningSessionReadRepository;
import com.qr_restaurant.table.repository.read.TableReadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional
class EndDiningSessionCommandImpl implements EndDiningSessionCommand {

    private final TableRepository tableRepository;
    private final TableReadRepository tableReadRepository;
    private final DiningSessionReadRepository diningSessionReadRepository;

    EndDiningSessionCommandImpl(TableRepository tableRepository,
                                TableReadRepository tableReadRepository,
                                DiningSessionReadRepository diningSessionReadRepository) {
        this.tableRepository = tableRepository;
        this.tableReadRepository = tableReadRepository;
        this.diningSessionReadRepository = diningSessionReadRepository;
    }

    @Override
    public void execute(DiningSessionId id) {
        var session = diningSessionReadRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No such dining session: " + id.value()));
        var table = tableReadRepository.findById(session.getTableId())
                .orElseThrow(() -> new IllegalStateException("Dining session has no table: " + id.value()));

        // An old session (the table has since been cleaned and re-seated) can't end the current one.
        var current = table.getCurrentSession();
        if (current == null || !current.getId().equals(id)) {
            throw new IllegalStateException("This dining session has already ended");
        }
        end(table);
    }

    @Override
    public void execute(TableId tableId) {
        var table = tableReadRepository.findById(tableId)
                .orElseThrow(() -> new NoSuchElementException("No such table: " + tableId.value()));
        end(table);
    }

    private void end(Table table) {
        // Registers TableStatusChanged and DiningSessionEnded; both are published on save, inside this
        // transaction, so DiningSessionEnded is stored in the event publication registry before commit.
        table.endSession();
        tableRepository.save(table);
    }
}
