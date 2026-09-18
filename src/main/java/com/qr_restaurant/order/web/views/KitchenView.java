package com.qr_restaurant.order.web.views;

import com.qr_restaurant.menu.use_cases.queries.GetMenuItemsQuery;
import com.qr_restaurant.menu.use_cases.queries.dtos.MenuItemView;
import com.qr_restaurant.menu.vo.MenuItemId;
import com.qr_restaurant.order.entities.Order;
import com.qr_restaurant.order.enums.OrderStatus;
import com.qr_restaurant.order.events.OrderStatusChanged;
import com.qr_restaurant.order.use_cases.commands.CancelOrderCommand;
import com.qr_restaurant.order.use_cases.commands.ConfirmOrderCommand;
import com.qr_restaurant.order.use_cases.commands.MarkOrderReadyCommand;
import com.qr_restaurant.order.use_cases.commands.ServeOrderCommand;
import com.qr_restaurant.order.use_cases.commands.dtos.CancelOrderDto;
import com.qr_restaurant.order.use_cases.queries.ViewIncomingOrdersQuery;
import com.qr_restaurant.order.web.OrderStatusBroadcaster;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;

import java.util.Map;
import java.util.stream.Collectors;

@Route("kitchen")
@PageTitle("Kitchen")
public class KitchenView extends VerticalLayout {

    private final ViewIncomingOrdersQuery viewIncomingOrdersQuery;
    private final ConfirmOrderCommand confirmOrderCommand;
    private final MarkOrderReadyCommand markOrderReadyCommand;
    private final ServeOrderCommand serveOrderCommand;
    private final CancelOrderCommand cancelOrderCommand;
    private final OrderStatusBroadcaster orderStatusBroadcaster;
    private final GetMenuItemsQuery getMenuItemsQuery;

    private final Grid<Order> grid = new Grid<>();
    private Registration orderUpdates;
    private Map<MenuItemId, String> menuItemNames = Map.of();

    public KitchenView(ViewIncomingOrdersQuery viewIncomingOrdersQuery, ConfirmOrderCommand confirmOrderCommand,
                        MarkOrderReadyCommand markOrderReadyCommand, ServeOrderCommand serveOrderCommand,
                        CancelOrderCommand cancelOrderCommand, OrderStatusBroadcaster orderStatusBroadcaster,
                        GetMenuItemsQuery getMenuItemsQuery) {
        this.viewIncomingOrdersQuery = viewIncomingOrdersQuery;
        this.confirmOrderCommand = confirmOrderCommand;
        this.markOrderReadyCommand = markOrderReadyCommand;
        this.serveOrderCommand = serveOrderCommand;
        this.cancelOrderCommand = cancelOrderCommand;
        this.orderStatusBroadcaster = orderStatusBroadcaster;
        this.getMenuItemsQuery = getMenuItemsQuery;

        add(new H2("Kitchen - Incoming Orders"), new Anchor("", "Customer view"), new Anchor("staff/tables", "Staff view"));

        grid.addColumn(o -> o.getId().value()).setHeader("Order");
        grid.addColumn(o -> o.getItems().size() + " item(s)").setHeader("Items");
        grid.addColumn(o -> o.getStatus().name()).setHeader("Status");
        grid.addComponentColumn(this::actions).setHeader("Actions");
        // Details row under an order; only opened for unconfirmed orders (see refresh()).
        grid.setItemDetailsRenderer(new ComponentRenderer<>(this::orderItems));
        grid.setDetailsVisibleOnClick(false);
        add(grid);

        refresh();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        var ui = attachEvent.getUI();
        // Events arrive on whichever thread changed the order (a customer's request, another kitchen
        // tab, or the async DiningSessionEnded listener); ui.access() locks this UI before updating it.
        orderUpdates = orderStatusBroadcaster.register(event -> ui.access(() -> onOrderChanged(event)));
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        orderUpdates.remove();
        orderUpdates = null;
        super.onDetach(detachEvent);
    }

    private void onOrderChanged(OrderStatusChanged event) {
        if (event.status() == OrderStatus.PENDING) {
            Notification.show("New order received", 3000, Notification.Position.TOP_END);
        }
        refresh();
    }

    private HorizontalLayout actions(Order order) {
        var layout = new HorizontalLayout();

        switch (order.getStatus()) {
            case PENDING -> {
                layout.add(actionButton("Confirm", order, o -> confirmOrderCommand.execute(o.getId())));
                layout.add(actionButton("Cancel", order,
                        o -> cancelOrderCommand.execute(o.getId(), new CancelOrderDto("kitchen", "Unable to fulfil"))));
            }
            case CONFIRMED -> {
                layout.add(actionButton("Mark Ready", order, o -> markOrderReadyCommand.execute(o.getId())));
                layout.add(actionButton("Cancel", order,
                        o -> cancelOrderCommand.execute(o.getId(), new CancelOrderDto("kitchen", "Unable to fulfil"))));
            }
            case READY -> layout.add(actionButton("Serve", order, o -> serveOrderCommand.execute(o.getId())));
            case SERVED, CANCELLED -> {
                // terminal states, nothing to do
            }
        }

        return layout;
    }

    private Button actionButton(String label, Order order, java.util.function.Consumer<Order> action) {
        return new Button(label, e -> {
            try {
                action.accept(order);
            } catch (IllegalStateException ex) {
                // The order changed after this row was drawn, e.g. its dining session just ended.
                Notification.show("Order was already updated: " + ex.getMessage());
            }
            refresh();
        });
    }

    private VerticalLayout orderItems(Order order) {
        var items = new VerticalLayout();
        items.setPadding(false);
        items.setSpacing(false);
        for (var item : order.getItems()) {
            var name = menuItemNames.getOrDefault(item.getMenuItemId(), item.getMenuItemId().value());
            items.add(new Span(item.getQuantity() + " × " + name));
        }
        return items;
    }

    private void refresh() {
        menuItemNames = getMenuItemsQuery.query(null, null).stream()
                .collect(Collectors.toMap(MenuItemView::id, MenuItemView::name));

        var orders = viewIncomingOrdersQuery.query((OrderStatus) null);
        grid.setItems(orders);
        // Show what was ordered so the kitchen can check it before confirming.
        orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.PENDING)
                .forEach(order -> grid.setDetailsVisible(order, true));
    }
}
