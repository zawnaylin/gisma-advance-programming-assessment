package com.qr_restaurant.table.application.use_cases.commands;

import com.qr_restaurant.table.application.vo.TableId;

/**
 * Creates, updates and deletes the restaurant's tables. The dining flow (seating guests,
 * ending sessions, cleaning) lives in the other commands of this package.
 */
public interface ManageTableCommand {
    /**
     * Adds a table, which starts out available.
     *
     * @param id       the id to use, or {@code null} to generate one
     * @param capacity how many guests it seats
     * @return the id of the new table
     * @throws IllegalStateException if a table with that id already exists
     */
    TableId create(TableId id, int capacity);

    /**
     * Changes how many guests a table seats. A table's status is derived from its dining
     * session and cannot be set directly.
     *
     * @throws java.util.NoSuchElementException if there is no table with that id
     */
    void updateCapacity(TableId id, int capacity);

    /**
     * Removes a table.
     *
     * @throws java.util.NoSuchElementException if there is no table with that id
     * @throws IllegalStateException            if guests are seated, or the table already has
     *                                          dining sessions that would lose their table
     */
    void delete(TableId id);
}
