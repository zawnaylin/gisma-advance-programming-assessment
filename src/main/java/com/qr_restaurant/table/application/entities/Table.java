package com.qr_restaurant.table.application.entities;

import com.qr_restaurant.common.Domain;
import com.qr_restaurant.common.StateMachine;
import com.qr_restaurant.table.application.enums.TableStatus;
import com.qr_restaurant.table.application.vo.TableId;
import lombok.Getter;


@Getter
public class Table extends Domain<TableId> {

    private static final StateMachine<TableStatus> STATUS_TRANSITION = StateMachine.builder(TableStatus.class)
            .allow(TableStatus.AVAILABLE, TableStatus.OCCUPIED)
            .allow(TableStatus.OCCUPIED, TableStatus.WAITING_FOR_CLEANING)
            .allow(TableStatus.WAITING_FOR_CLEANING, TableStatus.AVAILABLE)
            .build();

    private int capacity;
    private TableStatus status;

    public Table() {
        this.status = TableStatus.AVAILABLE;
    }

    public Table(TableId id, int capacity) {
        super(id);
        this.capacity = capacity;
        this.status = TableStatus.AVAILABLE;
    }

    public void take() {
        this.transitionTo(TableStatus.OCCUPIED);
    }

    public void finish() {
        this.transitionTo(TableStatus.WAITING_FOR_CLEANING);
    }

    public void clean() {
        this.transitionTo(TableStatus.AVAILABLE);
    }

    public void markCleaned() {
        if (this.status == TableStatus.OCCUPIED) {
            this.finish();
        }
        this.clean();
    }

    private void transitionTo(TableStatus target) {
        STATUS_TRANSITION.validate(this.status, target);
        this.status = target;
    }
}

