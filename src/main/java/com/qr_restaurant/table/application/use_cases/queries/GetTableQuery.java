package com.qr_restaurant.table.application.use_cases.queries;

import com.qr_restaurant.table.application.entities.Table;
import com.qr_restaurant.table.application.vo.TableId;

/**
 * Looks up a single table.
 */
public interface GetTableQuery {
    /**
     * @param id the table to look up
     * @return the table, or {@code null} if there is none with that id
     */
    Table query(TableId id);
}
