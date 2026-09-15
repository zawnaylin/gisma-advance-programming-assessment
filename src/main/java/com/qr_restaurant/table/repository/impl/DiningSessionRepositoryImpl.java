package com.qr_restaurant.table.repository.impl;

import com.qr_restaurant.common.InMemoryRepository;
import com.qr_restaurant.table.application.entities.DiningSession;
import com.qr_restaurant.table.application.enums.DiningStatus;
import com.qr_restaurant.table.application.vo.DiningSessionId;
import com.qr_restaurant.table.application.vo.TableId;
import com.qr_restaurant.table.repository.DiningSessionRepository;
import com.qr_restaurant.table.repository.read.DiningSessionReadRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
class DiningSessionRepositoryImpl extends InMemoryRepository<DiningSessionId, DiningSession>
        implements DiningSessionRepository, DiningSessionReadRepository {

    @Override
    public Optional<DiningSession> findActiveByTableId(TableId tableId) {
        return findAll().stream()
                .filter(session -> session.getTableId().equals(tableId))
                .filter(session -> session.getStatus() != DiningStatus.COMPLETED)
                .findFirst();
    }
}
