package com.qr_restaurant.table.application.use_cases.queries.impl;

import com.qr_restaurant.table.application.entities.QR;
import com.qr_restaurant.table.application.use_cases.queries.GetQRForTableQuery;
import com.qr_restaurant.table.application.vo.TableId;
import org.springframework.stereotype.Service;

@Service
class GetQRForTableQueryImpl implements GetQRForTableQuery {

    @Override
    public QR query(TableId id) {
        // TODO: implement
        return null;
    }
}
