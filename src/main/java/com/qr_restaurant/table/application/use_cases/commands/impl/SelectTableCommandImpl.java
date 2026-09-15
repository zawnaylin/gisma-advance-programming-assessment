package com.qr_restaurant.table.application.use_cases.commands.impl;

import com.qr_restaurant.table.application.entities.DiningSession;
import com.qr_restaurant.table.application.use_cases.commands.SelectTableCommand;
import com.qr_restaurant.table.application.vo.DiningSessionId;
import com.qr_restaurant.table.application.vo.TableId;
import com.qr_restaurant.table.repository.DiningSessionRepository;
import com.qr_restaurant.table.repository.TableRepository;
import com.qr_restaurant.table.repository.read.TableReadRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
class SelectTableCommandImpl implements SelectTableCommand {

    private final TableRepository tableRepository;
    private final TableReadRepository tableReadRepository;
    private final DiningSessionRepository diningSessionRepository;

    SelectTableCommandImpl(TableRepository tableRepository,
                            TableReadRepository tableReadRepository,
                            DiningSessionRepository diningSessionRepository) {
        this.tableRepository = tableRepository;
        this.tableReadRepository = tableReadRepository;
        this.diningSessionRepository = diningSessionRepository;
    }

    @Override
    public void execute(TableId id) {
        var table = tableReadRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No such table: " + id.value()));

        table.take();
        tableRepository.save(table);

        var session = new DiningSession(new DiningSessionId(UUID.randomUUID().toString()), id, 1);
        diningSessionRepository.save(session);
    }
}
