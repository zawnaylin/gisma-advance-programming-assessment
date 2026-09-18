package com.qr_restaurant.table.application.use_cases.queries.impl;

import com.qr_restaurant.table.application.entities.DiningSession;
import com.qr_restaurant.table.application.use_cases.queries.IsDiningSessionActiveQuery;
import com.qr_restaurant.table.application.vo.DiningSessionId;
import com.qr_restaurant.table.repository.read.DiningSessionReadRepository;
import org.springframework.stereotype.Service;

@Service
class IsDiningSessionActiveQueryImpl implements IsDiningSessionActiveQuery {

    private final DiningSessionReadRepository diningSessionReadRepository;

    IsDiningSessionActiveQueryImpl(DiningSessionReadRepository diningSessionReadRepository) {
        this.diningSessionReadRepository = diningSessionReadRepository;
    }

    @Override
    public boolean query(DiningSessionId id) {
        return diningSessionReadRepository.findById(id)
                .map(DiningSession::isActive)
                .orElse(false);
    }
}
