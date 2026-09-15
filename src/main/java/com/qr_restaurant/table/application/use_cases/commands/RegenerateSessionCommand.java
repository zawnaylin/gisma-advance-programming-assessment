package com.qr_restaurant.table.application.use_cases.commands;

import com.qr_restaurant.table.application.entities.DiningSession;
import com.qr_restaurant.table.application.vo.TableId;

public interface RegenerateSessionCommand {
    // Ends the table's current active session (if any) and starts a new one,
    // invalidating any previously issued QR code for this table.
    DiningSession execute(TableId tableId);
}
