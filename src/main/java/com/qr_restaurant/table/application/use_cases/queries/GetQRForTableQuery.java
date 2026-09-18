package com.qr_restaurant.table.application.use_cases.queries;

import com.qr_restaurant.table.application.entities.QR;
import com.qr_restaurant.table.application.vo.TableId;

/**
 * Builds the QR code data for a table's current dining session, so it can be shown or
 * printed again.
 */
public interface GetQRForTableQuery {
    /**
     * @param id the table to build a code for
     * @return the QR code data, or {@code null} if the table doesn't exist or has no session
     *         that accepts orders
     */
    QR query(TableId id);
}
