package com.qr_restaurant.table.repository.read;

import com.qr_restaurant.table.application.entities.DiningSession;
import com.qr_restaurant.table.application.vo.DiningSessionId;
import com.qr_restaurant.table.application.vo.TableId;

import java.util.List;
import java.util.Optional;

/**
 * Reads dining sessions. Read-only on purpose: sessions are created and changed through
 * their {@code Table}, and a table's current session is {@code Table.getCurrentSession()}.
 */
public interface DiningSessionReadRepository {

    /**
     * @return every one of them, in no particular order
     */
    List<DiningSession> findAll();

    /**
     * @param id the id to look for
     * @return the matching session, or empty if there is none
     */
    Optional<DiningSession> findById(DiningSessionId id);

    /**
     * @param tableId the table to list sessions for
     * @return the table's whole history, its current session included
     */
    List<DiningSession> findByTableId(TableId tableId);
}
