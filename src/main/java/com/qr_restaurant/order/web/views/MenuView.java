package com.qr_restaurant.order.web.views;

import com.qr_restaurant.menu.use_cases.queries.GetMenuItemsQuery;
import com.qr_restaurant.menu.use_cases.queries.dtos.MenuItemView;
import com.qr_restaurant.menu.vo.MenuItemId;
import com.qr_restaurant.order.use_cases.commands.PlaceOrderCommand;
import com.qr_restaurant.order.use_cases.commands.dtos.PlaceOrderDto;
import com.qr_restaurant.table.application.vo.DiningSessionId;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.LinkedHashMap;
import java.util.Map;

@Route("menu/:sessionId")
@PageTitle("Menu")
public class MenuView extends VerticalLayout implements BeforeEnterObserver {

    private final GetMenuItemsQuery getMenuItemsQuery;
    private final PlaceOrderCommand placeOrderCommand;

    private final Map<MenuItemId, IntegerField> quantityFields = new LinkedHashMap<>();
    private String sessionId;

    public MenuView(GetMenuItemsQuery getMenuItemsQuery, PlaceOrderCommand placeOrderCommand) {
        this.getMenuItemsQuery = getMenuItemsQuery;
        this.placeOrderCommand = placeOrderCommand;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        this.sessionId = event.getRouteParameters().get("sessionId").orElseThrow();
        buildUi();
    }

    private void buildUi() {
        removeAll();
        quantityFields.clear();

        add(new H2("Menu"));

        for (MenuItemView item : getMenuItemsQuery.query(null, null)) {
            var quantity = new IntegerField();
            quantity.setValue(0);
            quantity.setMin(0);
            quantity.setStepButtonsVisible(true);
            quantity.setWidth("6em");
            quantityFields.put(item.id(), quantity);

            var label = new Span(item.name() + " - $" + item.price());
            label.setWidth("20em");

            add(new HorizontalLayout(label, quantity));
        }

        var placeOrder = new Button("Place Order", e -> submitOrder());
        placeOrder.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(placeOrder);
    }

    private void submitOrder() {
        var lines = quantityFields.entrySet().stream()
                .filter(e -> e.getValue().getValue() != null && e.getValue().getValue() > 0)
                .map(e -> new PlaceOrderDto.Item(e.getKey(), e.getValue().getValue()))
                .toList();

        if (lines.isEmpty()) {
            Notification.show("Select at least one item");
            return;
        }

        var orderId = placeOrderCommand.execute(new PlaceOrderDto(new DiningSessionId(sessionId), lines));
        getUI().ifPresent(ui -> ui.navigate("orders/" + orderId.value()));
    }
}
