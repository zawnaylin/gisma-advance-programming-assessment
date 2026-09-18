package com.qr_restaurant.table.application.entities;

import com.qr_restaurant.common.Domain;
import com.qr_restaurant.table.application.enums.DiningStatus;
import com.qr_restaurant.table.application.enums.TableStatus;
import com.qr_restaurant.table.application.events.DiningSessionEnded;
import com.qr_restaurant.table.application.events.TableStatusChanged;
import com.qr_restaurant.table.application.vo.DiningSessionId;
import com.qr_restaurant.table.application.vo.TableId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Aggregate root for a table and its dining sessions. The table stores only which session it is in;
 * its status is derived from that session, so the two can never disagree.
 */
@Getter
@Entity
@jakarta.persistence.Table(name = "dining_tables")
public class Table extends Domain<TableId> {

    // Optimistic lock: an update fails if another transaction changed this row since it was read.
    // A wrapper type so Spring Data treats a null version as a new entity (persist, not merge).
    // Seating and cleaning change current_session_id, so concurrent attempts on the same table conflict here;
    // ending a session changes only the session row, which is guarded by the session's own version.
    @Version
    private Long version;

    private int capacity;

    // null while nobody is at the table. Completed sessions stay in dining_sessions as history.
    // The column is intentionally not unique (see V5 migration) so concurrent seating can't deadlock.
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinColumn(name = "current_session_id")
    private DiningSession currentSession;

    // Collected on each change and published by Spring Data on tableRepository.save(table),
    // so no command that changes a table can forget to announce it.
    @Transient
    @Getter(AccessLevel.NONE)
    private final List<Object> domainEvents = new ArrayList<>();

    protected Table() {
    }

    public Table(TableId id, int capacity) {
        super(id);
        this.capacity = capacity;
    }

    /**
     * Changes how many guests the table seats.
     */
    public void updateCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * The table's status, worked out from its current session rather than stored, so the two
     * can never disagree: no session is AVAILABLE, a session that still accepts orders is
     * OCCUPIED, and one that has ended is WAITING_FOR_CLEANING.
     *
     * @return the status the screens and the API show
     */
    public TableStatus getStatus() {
        if (currentSession == null) {
            return TableStatus.AVAILABLE;
        }
        return currentSession.getStatus() == DiningStatus.WAITING_FOR_CLEANING
                ? TableStatus.WAITING_FOR_CLEANING
                : TableStatus.OCCUPIED;
    }

    /**
     * Seats guests and starts their dining session, making the table OCCUPIED.
     *
     * @param sessionId      the id for the new session, which its QR code will encode
     * @param numberOfGuests how many guests sat down
     * @return the new session
     * @throws IllegalStateException if the table is not available
     */
    public DiningSession seat(DiningSessionId sessionId, int numberOfGuests) {
        if (currentSession != null) {
            throw new IllegalStateException("Table " + getId().value() + " is not available");
        }
        currentSession = new DiningSession(sessionId, getId(), numberOfGuests);
        statusChanged();
        return currentSession;
    }

    /**
     * Guests are done: the session stops accepting orders and the table waits to be cleaned.
     * Registers DiningSessionEnded for the other modules to react to.
     *
     * @throws IllegalStateException if nobody is seated at the table
     */
    public void endSession() {
        var session = requireSession();
        session.end();
        statusChanged();
        // Handled by other modules (e.g. order cancels unfinished orders) via the event publication registry.
        domainEvents.add(new DiningSessionEnded(session.getId(), getId()));
    }

    /**
     * The table has been cleaned: its session becomes history and the table is available again.
     *
     * @throws IllegalStateException if the table is not waiting to be cleaned
     */
    public void markCleaned() {
        requireSession().complete();
        currentSession = null;
        statusChanged();
    }

    private DiningSession requireSession() {
        if (currentSession == null) {
            throw new IllegalStateException("Table " + getId().value() + " has no dining session");
        }
        return currentSession;
    }

    private void statusChanged() {
        domainEvents.add(new TableStatusChanged(getId(), getStatus()));
    }

    @DomainEvents
    Collection<Object> domainEvents() {
        return List.copyOf(domainEvents);
    }

    @AfterDomainEventPublication
    void clearDomainEvents() {
        domainEvents.clear();
    }
}
