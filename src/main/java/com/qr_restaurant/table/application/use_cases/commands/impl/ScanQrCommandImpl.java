package com.qr_restaurant.table.application.use_cases.commands.impl;

import com.qr_restaurant.table.application.entities.DiningSession;
import com.qr_restaurant.table.application.use_cases.commands.ScanQrCommand;
import com.qr_restaurant.table.application.vo.DiningSessionId;
import com.qr_restaurant.table.application.vo.TableId;
import com.qr_restaurant.table.repository.read.DiningSessionReadRepository;
import org.springframework.stereotype.Service;

@Service
class ScanQrCommandImpl implements ScanQrCommand {

    private final DiningSessionReadRepository diningSessionReadRepository;

    ScanQrCommandImpl(DiningSessionReadRepository diningSessionReadRepository) {
        this.diningSessionReadRepository = diningSessionReadRepository;
    }

    @Override
    public DiningSession execute(TableId tableId, DiningSessionId sessionId) {
        var activeSession = diningSessionReadRepository.findActiveByTableId(tableId)
                .orElseThrow(() -> new IllegalStateException("No active session for table: " + tableId.value()));

        if (!activeSession.getId().equals(sessionId)) {
            throw new IllegalStateException("This QR code is no longer valid");
        }

        return activeSession;
    }
}
