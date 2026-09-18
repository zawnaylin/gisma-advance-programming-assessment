package com.qr_restaurant.table.application.use_cases.commands;

import com.qr_restaurant.table.application.vo.TableId;

/**
 * Seats guests at a free table, which starts a dining session and makes the table OCCUPIED.
 * The session's id is what the table's QR code encodes.
 */
public interface SelectTableCommand {
    /**
     * @param id the table to seat guests at
     * @throws java.util.NoSuchElementException if there is no table with that id
     * @throws IllegalStateException            if the table is not available
     * @throws org.springframework.dao.OptimisticLockingFailureException
     *         if another request seated guests at the same table first
     */
    void execute(TableId id);
}
