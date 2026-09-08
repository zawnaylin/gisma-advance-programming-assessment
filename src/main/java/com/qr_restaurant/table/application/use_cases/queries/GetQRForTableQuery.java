package com.qr_restaurant.table.application.use_cases.queries;

import com.qr_restaurant.table.application.use_cases.dtos.QRDto;
import com.qr_restaurant.table.application.vo.TableId;

public interface GetQRForTableQuery {
    QRDto query(TableId id);
}
