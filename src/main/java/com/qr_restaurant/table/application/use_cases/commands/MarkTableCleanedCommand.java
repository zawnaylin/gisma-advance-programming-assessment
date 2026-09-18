package com.qr_restaurant.table.application.use_cases.commands;

import com.qr_restaurant.table.application.vo.TableId;

/**
 * A waiter has cleaned the table: its dining session becomes history and the table is
 * available again. Only a table that is waiting to be cleaned can be cleaned.
 */
public interface MarkTableCleanedCommand {
    /**
     * @param id the table that was cleaned
     * @throws java.util.NoSuchElementException if there is no table with that id
     * @throws IllegalStateException            if the table is not waiting to be cleaned
     */
    void execute(TableId id);
}
