package com.qr_restaurant.table;

import com.qr_restaurant.TestcontainersConfiguration;
import com.qr_restaurant.table.application.entities.DiningSession;
import com.qr_restaurant.table.application.enums.DiningStatus;
import com.qr_restaurant.table.application.use_cases.commands.SelectTableCommand;
import com.qr_restaurant.table.application.vo.DiningSessionId;
import com.qr_restaurant.table.application.vo.TableId;
import com.qr_restaurant.table.repository.TableRepository;
import com.qr_restaurant.table.repository.read.DiningSessionReadRepository;
import com.qr_restaurant.table.repository.read.TableReadRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class OptimisticLockingTests {

    @Autowired
    TransactionTemplate transactionTemplate;
    @Autowired
    TableRepository tableRepository;
    @Autowired
    TableReadRepository tableReadRepository;
    @Autowired
    DiningSessionReadRepository diningSessionReadRepository;
    @Autowired
    SelectTableCommand selectTableCommand;

    @Test
    void concurrentSeatingAtSameTable_secondCommitFails() throws Exception {
        var tableId = new TableId("T3");
        var bothRead = new CyclicBarrier(2);

        // Both transactions see the table free at the same version before either writes.
        Callable<Void> seat = () -> {
            transactionTemplate.executeWithoutResult(tx -> {
                var table = tableReadRepository.findById(tableId).orElseThrow();
                await(bothRead);
                table.seat(new DiningSessionId(UUID.randomUUID().toString()), 2);
                tableRepository.save(table);
            });
            return null;
        };

        var failures = runConcurrently(List.of(seat, seat));

        assertThat(failures).singleElement().isInstanceOf(OptimisticLockingFailureException.class);
        var table = tableReadRepository.findById(tableId).orElseThrow();
        assertThat(table.getVersion()).isEqualTo(1L);
        // The loser's session insert was rolled back with its transaction.
        assertThat(openSessionsOf(tableId)).singleElement()
                .satisfies(session -> assertThat(session.getId()).isEqualTo(table.getCurrentSession().getId()));
    }

    @Test
    void concurrentEndsOfSameDiningSession_secondCommitFails() throws Exception {
        var tableId = new TableId("T1");
        selectTableCommand.execute(tableId);
        var sessionId = tableReadRepository.findById(tableId).orElseThrow().getCurrentSession().getId();
        var bothRead = new CyclicBarrier(2);

        Callable<Void> endSession = () -> {
            transactionTemplate.executeWithoutResult(tx -> {
                var table = tableReadRepository.findById(tableId).orElseThrow();
                await(bothRead);
                table.endSession();
                tableRepository.save(table);
            });
            return null;
        };

        var failures = runConcurrently(List.of(endSession, endSession));

        // Ending only changes the session row, so the session's version catches the race.
        assertThat(failures).singleElement().isInstanceOf(OptimisticLockingFailureException.class);
        var session = diningSessionReadRepository.findById(sessionId).orElseThrow();
        assertThat(session.getStatus()).isEqualTo(DiningStatus.WAITING_FOR_CLEANING);
        assertThat(session.getVersion()).isEqualTo(1L);
        assertThat(tableReadRepository.findById(tableId).orElseThrow().getVersion()).isEqualTo(1L);
    }

    @Test
    void manyCustomersSelectingSameTable_onlyOneGetsIt() throws Exception {
        var tableId = new TableId("T4");
        var start = new CountDownLatch(1);

        Callable<Void> select = () -> {
            start.await();
            selectTableCommand.execute(tableId);
            return null;
        };
        var tasks = Collections.nCopies(8, select);

        var executor = Executors.newFixedThreadPool(tasks.size());
        List<Future<Void>> futures;
        try {
            futures = tasks.stream().map(executor::submit).toList();
            start.countDown();
            executor.shutdown();
            assertThat(executor.awaitTermination(30, TimeUnit.SECONDS)).isTrue();
        } finally {
            executor.shutdownNow();
        }
        var failures = failuresOf(futures);

        // Losers either hit the version check (read before the winner committed)
        // or the table's "not available" rule (read after it committed).
        assertThat(failures).hasSize(tasks.size() - 1)
                .allMatch(e -> e instanceof OptimisticLockingFailureException || e instanceof IllegalStateException);
        // The losers' transactions rolled back, so no extra sessions were created.
        assertThat(openSessionsOf(tableId)).hasSize(1);
    }

    private List<DiningSession> openSessionsOf(TableId tableId) {
        return diningSessionReadRepository.findAll().stream()
                .filter(s -> s.getTableId().equals(tableId))
                .filter(s -> s.getStatus() != DiningStatus.COMPLETED)
                .toList();
    }

    private static List<Throwable> runConcurrently(List<Callable<Void>> tasks) throws Exception {
        var executor = Executors.newFixedThreadPool(tasks.size());
        try {
            return failuresOf(executor.invokeAll(tasks, 30, TimeUnit.SECONDS));
        } finally {
            executor.shutdownNow();
        }
    }

    private static List<Throwable> failuresOf(List<Future<Void>> futures) throws InterruptedException {
        var failures = new ArrayList<Throwable>();
        for (var future : futures) {
            try {
                future.get();
            } catch (ExecutionException e) {
                failures.add(e.getCause());
            }
        }
        return failures;
    }

    private static void await(CyclicBarrier barrier) {
        try {
            barrier.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException | BrokenBarrierException | TimeoutException e) {
            throw new IllegalStateException(e);
        }
    }
}
