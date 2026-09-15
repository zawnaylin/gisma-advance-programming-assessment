package com.qr_restaurant.order.web.views;

import com.qr_restaurant.order.entities.Order;
import com.qr_restaurant.order.enums.OrderStatus;
import com.qr_restaurant.order.use_cases.commands.CancelOrderCommand;
import com.qr_restaurant.order.use_cases.commands.ConfirmOrderCommand;
import com.qr_restaurant.order.use_cases.commands.MarkOrderReadyCommand;
import com.qr_restaurant.order.use_cases.commands.ServeOrderCommand;
import com.qr_restaurant.order.use_cases.commands.dtos.CancelOrderDto;
import com.qr_restaurant.order.use_cases.queries.ViewIncomingOrdersQuery;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("kitchen")
@PageTitle("Kitchen")
public class KitchenView extends VerticalLayout {

    private final ViewIncomingOrdersQuery viewIncomingOrdersQuery;
    private final ConfirmOrderCommand confirmOrderCommand;
    private final MarkOrderReadyCommand markOrderReadyCommand;
    private final ServeOrderCommand serveOrderCommand;
    private final CancelOrderCommand cancelOrderCommand;

    private final Grid<Order> grid = new Grid<>();

    public KitchenView(ViewIncomingOrdersQuery viewIncomingOrdersQuery, ConfirmOrderCommand confirmOrderCommand,
                        MarkOrderReadyCommand markOrderReadyCommand, ServeOrderCommand serveOrderCommand,
                        CancelOrderCommand cancelOrderCommand) {
        this.viewIncomingOrdersQuery = viewIncomingOrdersQuery;
        this.confirmOrderCommand = confirmOrderCommand;
        this.markOrderReadyCommand = markOrderReadyCommand;
        this.serveOrderCommand = serveOrderCommand;
        this.cancelOrderCommand = cancelOrderCommand;

        add(new H2("Kitchen - Incoming Orders"), new Anchor("", "Customer view"), new Anchor("staff/tables", "Staff view"));

        grid.addColumn(o -> o.getId().value()).setHeader("Order");
        grid.addColumn(o -> o.getItems().size() + " item(s)").setHeader("Items");
        grid.addColumn(o -> o.getStatus().name()).setHeader("Status");
        grid.addComponentColumn(this::actions).setHeader("Actions");
        add(grid);

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
            action.accept(order);
            refresh();
        });
    }

    private void refresh() {
        grid.setItems(viewIncomingOrdersQuery.query((OrderStatus) null));
    }
}
