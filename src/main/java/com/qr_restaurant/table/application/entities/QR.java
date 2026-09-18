package com.qr_restaurant.table.application.entities;

import lombok.Getter;

/**
 * The data behind a table's QR code: which table, which dining session, and the base URL
 * customers' phones should reach the app at.
 */
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

    /**
     * @return the URL the code encodes, which opens the menu for this table and session
     */
    public String generateUrl() {
        return baseUrl + "/scan?table=" + table.getId().value() + "&session=" + diningSession.getId().value();
    }
}
