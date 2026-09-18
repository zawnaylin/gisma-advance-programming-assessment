package com.qr_restaurant.table;

import com.qr_restaurant.TestcontainersConfiguration;
import com.qr_restaurant.table.application.entities.Table;
import com.qr_restaurant.table.application.enums.TableStatus;
import com.qr_restaurant.table.application.events.TableStatusChanged;
import com.qr_restaurant.table.application.use_cases.commands.SelectTableCommand;
import com.qr_restaurant.table.application.vo.DiningSessionId;
import com.qr_restaurant.table.application.vo.TableId;
import com.qr_restaurant.table.repository.TableRepository;
import com.qr_restaurant.table.repository.read.TableReadRepository;
import com.qr_restaurant.table.web.TableStatusBroadcaster;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class TableStatusBroadcasterTests {

    @Autowired TableStatusBroadcaster broadcaster;
    @Autowired SelectTableCommand selectTableCommand;
    @Autowired TableRepository tableRepository;
    @Autowired TableReadRepository tableReadRepository;
    @Autowired TransactionTemplate transactionTemplate;
    @Autowired JdbcTemplate jdbcTemplate;

    @Test
    void viewsAreNotifiedOnlyAfterTableStatusChangeCommits() {
        var tableId = new TableId("BROADCAST-1");
        tableRepository.save(new Table(tableId, 2));

        List<TableStatusChanged> received = new CopyOnWriteArrayList<>();
        var registration = broadcaster.register(received::add);
        try {
            // Rolled back: the change never happened, so no view may be told about it.
            transactionTemplate.executeWithoutResult(tx -> {
                var table = tableReadRepository.findById(tableId).orElseThrow();
                table.seat(new DiningSessionId(UUID.randomUUID().toString()), 2);
                tableRepository.save(table);
                tx.setRollbackOnly();
            });
            assertThat(received).isEmpty();

            selectTableCommand.execute(tableId);
            assertThat(received).containsExactly(new TableStatusChanged(tableId, TableStatus.OCCUPIED));
        } finally {
            registration.remove();
        }

        // Unregistered views receive nothing more.
        transactionTemplate.executeWithoutResult(tx -> {
            var table = tableReadRepository.findById(tableId).orElseThrow();
            table.endSession();
            tableRepository.save(table);
        });
        assertThat(received).hasSize(1);

        // UI refreshes are not stored in the event publication registry.
        var stored = jdbcTemplate.queryForObject(
                "select count(*) from event_publication where event_type like '%TableStatusChanged'", Integer.class);
        assertThat(stored).isZero();
    }
}
