package com.qr_restaurant.table.application.use_cases.queries;

import com.qr_restaurant.table.application.entities.Table;

import java.util.List;

/**
 * Lists the restaurant's tables with their current status, for the table and staff screens.
 */
public interface ShowTablesQuery {
    /**
     * @return every table, sorted by id
     */
    List<Table> query();
}
