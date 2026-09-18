package com.qr_restaurant.table.application.entities;

import com.qr_restaurant.common.Domain;
import com.qr_restaurant.common.StateMachine;
import com.qr_restaurant.table.application.enums.DiningStatus;
import com.qr_restaurant.table.application.vo.DiningSessionId;
import com.qr_restaurant.table.application.vo.TableId;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.Date;

/**
 * Part of the {@link Table} aggregate: created and moved through its lifecycle only by its table,
 * which derives its own status from the session's.
 */
@Getter
@Entity
@jakarta.persistence.Table(name = "dining_sessions")
public class DiningSession extends Domain<DiningSessionId> {

    private static final StateMachine<DiningStatus> TRANSITION_CHECKER = StateMachine.builder(DiningStatus.class)
            .allow(DiningStatus.ACTIVE, DiningStatus.WAITING_FOR_CHECK, DiningStatus.WAITING_FOR_CLEANING)
            .allow(DiningStatus.WAITING_FOR_CHECK, DiningStatus.WAITING_FOR_CLEANING)
            .allow(DiningStatus.WAITING_FOR_CLEANING, DiningStatus.COMPLETED)
            .build();

    // Optimistic lock, see Table.
    @Version
    private Long version;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "table_id"))
    private TableId tableId;
    @Enumerated(EnumType.STRING)
    private DiningStatus status;
    private Date startTime;
    private Date endTime;
    private int numberOfGuests;

    protected DiningSession() {
    }

    DiningSession(DiningSessionId id, TableId tableId, int numberOfGuests) {
        super(id);
        this.tableId = tableId;
        this.numberOfGuests = numberOfGuests;
        this.startTime = new Date();
        this.status = DiningStatus.ACTIVE;
    }

    void requestCheck() {
        this.transitionTo(DiningStatus.WAITING_FOR_CHECK);
    }

    void end() {
        this.transitionTo(DiningStatus.WAITING_FOR_CLEANING);
        this.endTime = new Date();
    }

    void complete() {
        this.transitionTo(DiningStatus.COMPLETED);
    }

    /**
     * @return whether customers may still order in this session
     */
    public boolean isActive() {
        return this.status == DiningStatus.ACTIVE;
    }

    private void transitionTo(DiningStatus target) {
        TRANSITION_CHECKER.validate(this.status, target);
        this.status = target;
    }
}
