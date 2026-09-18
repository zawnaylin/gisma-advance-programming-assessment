package com.qr_restaurant.table.application.enums;

public enum DiningStatus {
    ACTIVE,
    WAITING_FOR_CHECK,
    // Guests are done and no more orders are accepted; the table still has to be cleaned.
    WAITING_FOR_CLEANING,
    COMPLETED,
}
