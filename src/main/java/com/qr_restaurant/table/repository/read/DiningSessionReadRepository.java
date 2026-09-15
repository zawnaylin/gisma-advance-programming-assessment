package com.qr_restaurant.table.repository.read;

import com.qr_restaurant.table.application.entities.DiningSession;
import com.qr_restaurant.table.application.vo.DiningSessionId;
import com.qr_restaurant.table.application.vo.TableId;

import java.util.List;
import java.util.Optional;

public interface DiningSessionReadRepository {

    List<DiningSession> findAll();

    Optional<DiningSession> findById(DiningSessionId id);

    Optional<DiningSession> findActiveByTableId(TableId tableId);
}
