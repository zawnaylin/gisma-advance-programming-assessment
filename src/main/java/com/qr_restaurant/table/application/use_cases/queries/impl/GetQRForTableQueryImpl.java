package com.qr_restaurant.table.application.use_cases.queries.impl;

import com.qr_restaurant.table.application.use_cases.dtos.QRDto;
import com.qr_restaurant.table.application.use_cases.queries.GetQRForTableQuery;
import com.qr_restaurant.table.application.vo.TableId;
import org.springframework.stereotype.Service;

@Service
class GetQRForTableQueryImpl implements GetQRForTableQuery {

    @Override
    public QRDto query(TableId id) {
        // TODO: implement
        return null;
    }
}
