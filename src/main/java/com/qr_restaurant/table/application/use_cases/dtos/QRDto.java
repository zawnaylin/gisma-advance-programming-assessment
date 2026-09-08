package com.qr_restaurant.table.application.use_cases.dtos;

import com.qr_restaurant.table.application.vo.DiningSessionId;
import com.qr_restaurant.table.application.vo.TableId;

public record QRDto(
        TableId tableId,
        DiningSessionId diningSessionId,
        String url
) {
}
