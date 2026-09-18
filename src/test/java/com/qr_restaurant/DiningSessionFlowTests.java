package com.qr_restaurant;

import com.qr_restaurant.menu.vo.MenuItemId;
import com.qr_restaurant.order.enums.OrderStatus;
import com.qr_restaurant.order.use_cases.commands.ConfirmOrderCommand;
import com.qr_restaurant.order.use_cases.commands.MarkOrderReadyCommand;
import com.qr_restaurant.order.use_cases.commands.PlaceOrderCommand;
import com.qr_restaurant.order.use_cases.commands.ServeOrderCommand;
import com.qr_restaurant.order.use_cases.commands.dtos.PlaceOrderDto;
import com.qr_restaurant.order.use_cases.queries.TrackOrderStatusQuery;
import com.qr_restaurant.order.vo.OrderId;
import com.qr_restaurant.table.application.entities.Table;
import com.qr_restaurant.table.application.enums.DiningStatus;
import com.qr_restaurant.table.application.enums.TableStatus;
import com.qr_restaurant.table.application.events.DiningSessionEnded;
import com.qr_restaurant.table.application.use_cases.commands.EndDiningSessionCommand;
import com.qr_restaurant.table.application.use_cases.commands.MarkTableCleanedCommand;
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
import org.springframework.modulith.events.CompletedEventPublications;
import org.springframework.modulith.test.EnableScenarios;
import org.springframework.modulith.test.Scenario;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@EnableScenarios
@Import(TestcontainersConfiguration.class)
class DiningSessionFlowTests {

    @Autowired
    SelectTableCommand selectTableCommand;
    @Autowired
    EndDiningSessionCommand endDiningSessionCommand;
    @Autowired
    MarkTableCleanedCommand markTableCleanedCommand;
    @Autowired
    PlaceOrderCommand placeOrderCommand;
    @Autowired
    ConfirmOrderCommand confirmOrderCommand;
    @Autowired
    MarkOrderReadyCommand markOrderReadyCommand;
    @Autowired
    ServeOrderCommand serveOrderCommand;
    @Autowired
    TrackOrderStatusQuery trackOrderStatusQuery;
    @Autowired
    TableRepository tableRepository;
    @Autowired
    TableReadRepository tableReadRepository;
    @Autowired
    DiningSessionReadRepository diningSessionReadRepository;
    @Autowired
    CompletedEventPublications completedEventPublications;

    @Test
    void endingSessionCancelsUnfinishedOrdersAndTableCanThenBeCleaned(Scenario scenario) {
        var tableId = new TableId("T2");
        selectTableCommand.execute(tableId);
        var sessionId = currentSessionIdOf(tableId);

        var pending = placeOrder(sessionId);
        var confirmed = placeOrder(sessionId);
        confirmOrderCommand.execute(confirmed);
        var ready = placeOrder(sessionId);
        confirmOrderCommand.execute(ready);
        markOrderReadyCommand.execute(ready);
        var served = placeOrder(sessionId);
        confirmOrderCommand.execute(served);
        markOrderReadyCommand.execute(served);
        serveOrderCommand.execute(served);

        // A table can't be cleaned while guests are still dining.
        assertThatIllegalStateException().isThrownBy(() -> markTableCleanedCommand.execute(tableId));

        scenario.stimulate(() -> endDiningSessionCommand.execute(sessionId))
                .andWaitAtMost(Duration.ofSeconds(10))
                // The order module's listener runs asynchronously after the table transaction commits.
                .andWaitForStateChange(() -> statusOf(pending) == OrderStatus.CANCELLED
                        && statusOf(confirmed) == OrderStatus.CANCELLED)
                .andVerifyEvents(events -> assertThat(events)
                        .contains(DiningSessionEnded.class).matchingValue(DiningSessionEnded::sessionId, sessionId));

        assertThat(statusOf(ready)).isEqualTo(OrderStatus.READY);
        assertThat(statusOf(served)).isEqualTo(OrderStatus.SERVED);
        assertThat(trackOrderStatusQuery.query(pending).getCancelledBy()).isEqualTo("system");

        // The table's status is derived from the session's new stage.
        var session = diningSessionReadRepository.findById(sessionId).orElseThrow();
        assertThat(session.getStatus()).isEqualTo(DiningStatus.WAITING_FOR_CLEANING);
        assertThat(session.getEndTime()).isNotNull();
        assertThat(tableReadRepository.findById(tableId).orElseThrow().getStatus())
                .isEqualTo(TableStatus.WAITING_FOR_CLEANING);

        // The session is locked: no more orders, and it can't be ended twice.
        assertThatIllegalStateException().isThrownBy(() -> placeOrder(sessionId));
        assertThatIllegalStateException().isThrownBy(() -> endDiningSessionCommand.execute(sessionId));

        // The event publication registry marks the event completed once the listener succeeded.
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                assertThat(completedEventPublications.findAll())
                        .anyMatch(publication -> publication.getEvent() instanceof DiningSessionEnded ended
                                && ended.sessionId().equals(sessionId)));

        markTableCleanedCommand.execute(tableId);
        var cleaned = tableReadRepository.findById(tableId).orElseThrow();
        assertThat(cleaned.getStatus()).isEqualTo(TableStatus.AVAILABLE);
        assertThat(cleaned.getCurrentSession()).isNull();
        assertThat(diningSessionReadRepository.findById(sessionId).orElseThrow().getStatus())
                .isEqualTo(DiningStatus.COMPLETED);

        // Next guests: an old session id (e.g. from a stale menu page) can't end the new session.
        selectTableCommand.execute(tableId);
        var nextSessionId = currentSessionIdOf(tableId);
        assertThat(nextSessionId).isNotEqualTo(sessionId);
        assertThatIllegalStateException().isThrownBy(() -> endDiningSessionCommand.execute(sessionId));
        assertThat(tableReadRepository.findById(tableId).orElseThrow().getStatus()).isEqualTo(TableStatus.OCCUPIED);
    }

    @Test
    void staffCanEndTheActiveSessionOfATable(Scenario scenario) {
        var tableId = new TableId("STAFF-END-1");
        tableRepository.save(new Table(tableId, 4));

        // Nothing to end before anyone sits down.
        assertThatIllegalStateException().isThrownBy(() -> endDiningSessionCommand.execute(tableId));

        selectTableCommand.execute(tableId);
        var sessionId = currentSessionIdOf(tableId);
        var pending = placeOrder(sessionId);

        scenario.stimulate(() -> endDiningSessionCommand.execute(tableId))
                .andWaitAtMost(Duration.ofSeconds(10))
                // Same event and listener as the customer path.
                .andWaitForStateChange(() -> statusOf(pending) == OrderStatus.CANCELLED)
                .andVerifyEvents(events -> assertThat(events)
                        .contains(DiningSessionEnded.class).matchingValue(DiningSessionEnded::tableId, tableId));

        assertThat(diningSessionReadRepository.findById(sessionId).orElseThrow().getStatus())
                .isEqualTo(DiningStatus.WAITING_FOR_CLEANING);
        assertThat(tableReadRepository.findById(tableId).orElseThrow().getStatus())
                .isEqualTo(TableStatus.WAITING_FOR_CLEANING);
        assertThatIllegalStateException().isThrownBy(() -> endDiningSessionCommand.execute(tableId));
    }

    private DiningSessionId currentSessionIdOf(TableId tableId) {
        return tableReadRepository.findById(tableId).orElseThrow().getCurrentSession().getId();
    }

    private OrderId placeOrder(DiningSessionId sessionId) {
        return placeOrderCommand.execute(new PlaceOrderDto(sessionId,
                List.of(new PlaceOrderDto.Item(new MenuItemId("iced-tea"), 1))));
    }

    private OrderStatus statusOf(OrderId id) {
        return trackOrderStatusQuery.query(id).getStatus();
    }
}
