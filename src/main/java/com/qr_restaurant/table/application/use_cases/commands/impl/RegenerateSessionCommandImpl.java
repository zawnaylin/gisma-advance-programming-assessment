package com.qr_restaurant.table.application.use_cases.commands.impl;

import com.qr_restaurant.table.application.entities.DiningSession;
import com.qr_restaurant.table.application.use_cases.commands.RegenerateSessionCommand;
import com.qr_restaurant.table.application.vo.DiningSessionId;
import com.qr_restaurant.table.application.vo.TableId;
import com.qr_restaurant.table.repository.DiningSessionRepository;
import com.qr_restaurant.table.repository.read.DiningSessionReadRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
class RegenerateSessionCommandImpl implements RegenerateSessionCommand {

    private final DiningSessionRepository diningSessionRepository;
    private final DiningSessionReadRepository diningSessionReadRepository;

    RegenerateSessionCommandImpl(DiningSessionRepository diningSessionRepository,
                                  DiningSessionReadRepository diningSessionReadRepository) {
        this.diningSessionRepository = diningSessionRepository;
        this.diningSessionReadRepository = diningSessionReadRepository;
    }

    @Override
    public DiningSession execute(TableId tableId) {
        var currentSession = diningSessionReadRepository.findActiveByTableId(tableId)
                .orElseThrow(() -> new IllegalStateException("Table has no active session to regenerate: " + tableId.value()));

        currentSession.complete();
        diningSessionRepository.save(currentSession);

        var newSession = new DiningSession(new DiningSessionId(UUID.randomUUID().toString()), tableId, currentSession.getNumberOfGuests());
        diningSessionRepository.save(newSession);

        return newSession;
    }
}
