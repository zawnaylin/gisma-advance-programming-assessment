package com.qr_restaurant.table.entities;

import com.qr_restaurant.common.Domain;
import com.qr_restaurant.table.enums.DiningStatus;
import com.qr_restaurant.table.vo.DiningSessionId;

import java.util.Date;

public class DiningSession extends Domain<DiningSessionId> {

    private DiningStatus status;
    private Date startTime;
    private Date endTime;
    private int numberOfGuests;
}