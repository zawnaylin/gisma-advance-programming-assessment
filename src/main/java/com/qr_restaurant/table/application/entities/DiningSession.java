package com.qr_restaurant.table.application.entities;

import com.qr_restaurant.common.Domain;
import com.qr_restaurant.common.StateMachine;
import com.qr_restaurant.table.application.enums.DiningStatus;
import com.qr_restaurant.table.application.vo.DiningSessionId;
import com.qr_restaurant.table.application.vo.TableId;
import lombok.Getter;

import java.util.Date;

@Getter
public class DiningSession extends Domain<DiningSessionId> {

    private static final StateMachine<DiningStatus> TRANSITION_CHECKER = StateMachine.builder(DiningStatus.class)
            .allow(DiningStatus.ACTIVE, DiningStatus.WAITING_FOR_CHECK, DiningStatus.COMPLETED)
            .allow(DiningStatus.WAITING_FOR_CHECK, DiningStatus.COMPLETED)
            .build();

    private TableId tableId;
    private DiningStatus status;
    private Date startTime;
    private Date endTime;
    private int numberOfGuests;

    public DiningSession() {
        this.status = DiningStatus.ACTIVE;
    }

    public DiningSession(DiningSessionId id, TableId tableId, int numberOfGuests) {
        super(id);
        this.tableId = tableId;
        this.numberOfGuests = numberOfGuests;
        this.startTime = new Date();
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