package com.qr_restaurant.table.application.use_cases.queries.impl;

import com.qr_restaurant.table.application.entities.QR;
import com.qr_restaurant.table.application.use_cases.queries.GetQRForTableQuery;
import com.qr_restaurant.table.application.vo.TableId;
import com.qr_restaurant.table.repository.read.DiningSessionReadRepository;
import com.qr_restaurant.table.repository.read.TableReadRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
class GetQRForTableQueryImpl implements GetQRForTableQuery {

    private final TableReadRepository tableReadRepository;
    private final DiningSessionReadRepository diningSessionReadRepository;
    private final String baseUrl;

    GetQRForTableQueryImpl(TableReadRepository tableReadRepository,
                            DiningSessionReadRepository diningSessionReadRepository,
                            @Value("${app.base-url}") String baseUrl) {
        this.tableReadRepository = tableReadRepository;
        this.diningSessionReadRepository = diningSessionReadRepository;
        this.baseUrl = baseUrl;
    }

    @Override
    public QR query(TableId id) {
        var table = tableReadRepository.findById(id).orElse(null);
        if (table == null) {
            return null;
        }

        var session = diningSessionReadRepository.findActiveByTableId(id).orElse(null);
        if (session == null) {
            return null;
        }

        return new QR(table, session, baseUrl);
    }
}
