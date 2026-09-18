package com.qr_restaurant.table.application.events;

import com.qr_restaurant.table.application.enums.TableStatus;
import com.qr_restaurant.table.application.vo.TableId;

/**
 * Registered by {@code Table} on every status transition and published by Spring Data when the table is saved.
 *
 * @param tableId the table that changed
 * @param status  the status it now has
 */
public record TableStatusChanged(TableId tableId, TableStatus status) {
}
