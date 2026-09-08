package com.qr_restaurant.table.application.use_cases.queries;

import com.qr_restaurant.table.application.entities.QR;
import com.qr_restaurant.table.application.vo.TableId;

public interface GetQRForTableQuery {
    QR query(TableId id);
}
