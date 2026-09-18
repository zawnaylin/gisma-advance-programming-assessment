package com.qr_restaurant.table.application.use_cases.commands.impl;

import com.qr_restaurant.table.application.entities.DiningSession;
import com.qr_restaurant.table.application.use_cases.commands.ScanQrCommand;
import com.qr_restaurant.table.application.vo.DiningSessionId;
import com.qr_restaurant.table.application.vo.TableId;
import com.qr_restaurant.table.repository.read.TableReadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
class ScanQrCommandImpl implements ScanQrCommand {

    private final TableReadRepository tableReadRepository;

    ScanQrCommandImpl(TableReadRepository tableReadRepository) {
        this.tableReadRepository = tableReadRepository;
    }

    @Override
    public DiningSession execute(TableId tableId, DiningSessionId sessionId) {
        var session = tableReadRepository.findById(tableId)
                .map(table -> table.getCurrentSession())
                .filter(DiningSession::isActive)
                .orElseThrow(() -> new IllegalStateException("No active session for table: " + tableId.value()));

        if (!session.getId().equals(sessionId)) {
            throw new IllegalStateException("This QR code is no longer valid");
        }

        return session;
    }
}
