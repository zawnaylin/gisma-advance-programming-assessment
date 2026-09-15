package com.qr_restaurant.order.web.views;

import com.qr_restaurant.order.entities.OrderItem;
import com.qr_restaurant.order.use_cases.queries.TrackOrderStatusQuery;
import com.qr_restaurant.order.vo.OrderId;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("orders/:orderId")
@PageTitle("Order Status")
public class OrderStatusView extends VerticalLayout implements BeforeEnterObserver {

    private final TrackOrderStatusQuery trackOrderStatusQuery;
    private String orderId;

    public OrderStatusView(TrackOrderStatusQuery trackOrderStatusQuery) {
        this.trackOrderStatusQuery = trackOrderStatusQuery;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        this.orderId = event.getRouteParameters().get("orderId").orElseThrow();
        buildUi();
    }

    private void buildUi() {
        removeAll();

        var order = trackOrderStatusQuery.query(new OrderId(orderId));
        if (order == null) {
            add(new H2("Order not found"));
            return;
        }

        add(new H2("Order " + order.getId().value()));
        add(new Span("Status: " + order.getStatus().name()));

        double total = 0;
        for (OrderItem item : order.getItems()) {
            add(new Paragraph(item.getQuantity() + "x " + item.getMenuItemId().value()
                    + " - $" + (item.getPrice() * item.getQuantity())));
            total += item.getPrice() * item.getQuantity();
        }
        add(new Span("Total: $" + total));

        add(new Button("Refresh", e -> buildUi()));
    }
}
