package com.qr_restaurant.order;

import com.qr_restaurant.TestcontainersConfiguration;
import com.qr_restaurant.menu.vo.MenuItemId;
import com.qr_restaurant.order.enums.OrderStatus;
import com.qr_restaurant.order.events.OrderStatusChanged;
import com.qr_restaurant.order.use_cases.commands.ConfirmOrderCommand;
import com.qr_restaurant.order.use_cases.commands.PlaceOrderCommand;
import com.qr_restaurant.order.use_cases.commands.dtos.PlaceOrderDto;
import com.qr_restaurant.order.use_cases.queries.ViewIncomingOrdersQuery;
import com.qr_restaurant.order.web.OrderStatusBroadcaster;
import com.qr_restaurant.table.application.entities.Table;
import com.qr_restaurant.table.application.use_cases.commands.EndDiningSessionCommand;
import com.qr_restaurant.table.application.use_cases.commands.SelectTableCommand;
import com.qr_restaurant.table.application.vo.TableId;
import com.qr_restaurant.table.repository.TableRepository;
import com.qr_restaurant.table.repository.read.TableReadRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class OrderStatusBroadcasterTests {

    @Autowired OrderStatusBroadcaster broadcaster;
    @Autowired PlaceOrderCommand placeOrderCommand;
    @Autowired ConfirmOrderCommand confirmOrderCommand;
    @Autowired ViewIncomingOrdersQuery viewIncomingOrdersQuery;
    @Autowired TableRepository tableRepository;
    @Autowired SelectTableCommand selectTableCommand;
    @Autowired EndDiningSessionCommand endDiningSessionCommand;
    @Autowired TableReadRepository tableReadRepository;

    @Test
    void kitchenIsNotifiedOfNewOrdersStatusChangesAndSessionEndCancellations() {
        var tableId = new TableId("KITCHEN-1");
        tableRepository.save(new Table(tableId, 2));
        selectTableCommand.execute(tableId);
        var sessionId = tableReadRepository.findById(tableId).orElseThrow().getCurrentSession().getId();

        List<OrderStatusChanged> received = new CopyOnWriteArrayList<>();
        var registration = broadcaster.register(received::add);
        try {
            var first = placeOrderCommand.execute(new PlaceOrderDto(sessionId,
                    List.of(new PlaceOrderDto.Item(new MenuItemId("iced-tea"), 1))));
            var second = placeOrderCommand.execute(new PlaceOrderDto(sessionId,
                    List.of(new PlaceOrderDto.Item(new MenuItemId("spring-rolls"), 2))));
            confirmOrderCommand.execute(first);

            assertThat(received).containsExactly(
                    new OrderStatusChanged(first, OrderStatus.PENDING),
                    new OrderStatusChanged(second, OrderStatus.PENDING),
                    new OrderStatusChanged(first, OrderStatus.CONFIRMED));

            // Cancellations by the async DiningSessionEnded listener reach the kitchen too.
            endDiningSessionCommand.execute(tableId);
            await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> assertThat(received).contains(
                    new OrderStatusChanged(first, OrderStatus.CANCELLED),
                    new OrderStatusChanged(second, OrderStatus.CANCELLED)));

            // Oldest first, so the kitchen grid keeps a stable order while refreshing live.
            var ids = viewIncomingOrdersQuery.query(null).stream().map(order -> order.getId()).toList();
            assertThat(ids.indexOf(first)).isLessThan(ids.indexOf(second));
        } finally {
            registration.remove();
        }
    }
}
