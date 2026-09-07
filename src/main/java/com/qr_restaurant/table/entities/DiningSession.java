package com.qr_restaurant.table.entities;

import com.qr_restaurant.common.Domain;
import com.qr_restaurant.common.StateMachine;
import com.qr_restaurant.table.enums.DiningStatus;
import com.qr_restaurant.table.vo.DiningSessionId;

import java.util.Date;

public class DiningSession extends Domain<DiningSessionId> {

    private static final StateMachine<DiningStatus> TRANSITION_CHECKER = StateMachine.builder(DiningStatus.class)
            .allow(DiningStatus.ACTIVE, DiningStatus.WAITING_FOR_CHECK)
            .allow(DiningStatus.WAITING_FOR_CHECK, DiningStatus.COMPLETED)
            .build();

    private DiningStatus status;
    private Date startTime;
    private Date endTime;
    private int numberOfGuests;

    public DiningSession() {
        this.status = DiningStatus.ACTIVE;
    }

    public void requestCheck() {
        this.transitionTo(DiningStatus.WAITING_FOR_CHECK);
    }

    public void complete() {
        this.transitionTo(DiningStatus.COMPLETED);
    }

    private void transitionTo(DiningStatus target) {
        TRANSITION_CHECKER.validate(this.status, target);
        this.status = target;
    }
}