package com.qr_restaurant.table.application.entities;

import lombok.Getter;

@Getter
public class QR {
    private final Table table;
    private final DiningSession diningSession;
    private final String baseUrl;

    public QR(Table table, DiningSession diningSession, String baseUrl) {
        this.table = table;
        this.diningSession = diningSession;
        this.baseUrl = baseUrl;
    }

    public String generateUrl() {
        return baseUrl + "/scan?table=" + table.getId().value() + "&session=" + diningSession.getId().value();
    }
}
