package com.qr_restaurant.table.application.use_cases.commands;

import com.qr_restaurant.table.application.entities.DiningSession;
import com.qr_restaurant.table.application.vo.DiningSessionId;
import com.qr_restaurant.table.application.vo.TableId;

public interface ScanQrCommand {
    // sessionId must match the table's currently active session -
    // a stale/previously-cleaned QR should be rejected.
    DiningSession execute(TableId tableId, DiningSessionId sessionId);
}
