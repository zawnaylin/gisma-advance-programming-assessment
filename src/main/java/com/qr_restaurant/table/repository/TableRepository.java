package com.qr_restaurant.table.repository;

import com.qr_restaurant.table.application.entities.Table;
import com.qr_restaurant.table.application.vo.TableId;

/**
 * Stores tables together with their dining session, since the table is the aggregate root.
 * Saving also publishes the table's recorded events (TableStatusChanged, DiningSessionEnded).
 */
public interface TableRepository {

    /**
     * Inserts a new table or saves changes to an existing one, including its current session.
     *
     * @return the stored table
     * @throws org.springframework.dao.OptimisticLockingFailureException
     *         if another transaction changed the table since it was read
     */
    Table save(Table table);

    /**
     * Removes a table. Does nothing if it is already gone.
     */
    void deleteById(TableId id);
}
