package com.qr_restaurant.order.web.views;

import com.qr_restaurant.menu.use_cases.queries.GetMenuItemsQuery;
import com.qr_restaurant.menu.use_cases.queries.dtos.MenuItemView;
import com.qr_restaurant.menu.vo.MenuItemId;
import com.qr_restaurant.order.use_cases.commands.PlaceOrderCommand;
import com.qr_restaurant.order.use_cases.commands.dtos.PlaceOrderDto;
import com.qr_restaurant.order.use_cases.queries.ViewSessionOrdersQuery;
import com.qr_restaurant.table.application.use_cases.commands.EndDiningSessionCommand;
import com.qr_restaurant.table.application.use_cases.queries.IsDiningSessionActiveQuery;
import com.qr_restaurant.table.application.vo.DiningSessionId;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.LinkedHashMap;
import java.util.Map;

@Route("menu/:sessionId")
@PageTitle("Menu")
public class MenuView extends VerticalLayout implements BeforeEnterObserver {

    private final GetMenuItemsQuery getMenuItemsQuery;
    private final PlaceOrderCommand placeOrderCommand;
    private final ViewSessionOrdersQuery viewSessionOrdersQuery;
    private final IsDiningSessionActiveQuery isDiningSessionActiveQuery;
    private final EndDiningSessionCommand endDiningSessionCommand;

    private final Map<MenuItemId, IntegerField> quantityFields = new LinkedHashMap<>();
    private DiningSessionId sessionId;

    public MenuView(GetMenuItemsQuery getMenuItemsQuery, PlaceOrderCommand placeOrderCommand,
                    ViewSessionOrdersQuery viewSessionOrdersQuery,
                    IsDiningSessionActiveQuery isDiningSessionActiveQuery,
                    EndDiningSessionCommand endDiningSessionCommand) {
        this.getMenuItemsQuery = getMenuItemsQuery;
        this.placeOrderCommand = placeOrderCommand;
        this.viewSessionOrdersQuery = viewSessionOrdersQuery;
        this.isDiningSessionActiveQuery = isDiningSessionActiveQuery;
        this.endDiningSessionCommand = endDiningSessionCommand;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        this.sessionId = new DiningSessionId(event.getRouteParameters().get("sessionId").orElseThrow());

        if (isDiningSessionActiveQuery.query(sessionId)) {
            buildUi();
        } else {
            showSessionEnded();
        }
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

        var endOrder = new Button("End order", e -> confirmEndOrder());
        endOrder.addThemeVariants(ButtonVariant.LUMO_ERROR);

        add(new HorizontalLayout(placeOrder, endOrder));
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

        try {
            var orderId = placeOrderCommand.execute(new PlaceOrderDto(sessionId, lines));
            getUI().ifPresent(ui -> ui.navigate("orders/" + orderId.value()));
        } catch (IllegalStateException e) {
            // The session was ended (e.g. from another phone at the same table) after this page loaded.
            showSessionEnded();
        }
    }

    private void confirmEndOrder() {
        var unfinishedOrders = viewSessionOrdersQuery.query(sessionId).stream()
                .filter(order -> !order.isFinished())
                .count();

        if (unfinishedOrders == 0) {
            endOrder();
            return;
        }

        var dialog = new ConfirmDialog();
        dialog.setHeader("End your order?");
        dialog.setText(unfinishedOrders + " order(s) are still being prepared. Are you sure you want to end? "
                + "Orders the kitchen hasn't finished will be cancelled.");
        dialog.setCancelable(true);
        dialog.setConfirmText("End order");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(e -> endOrder());
        dialog.open();
    }

    private void endOrder() {
        try {
            endDiningSessionCommand.execute(sessionId);
        } catch (OptimisticLockingFailureException | IllegalStateException e) {
            // Someone else at the table already ended the session.
            Notification.show("This dining session has already ended");
        }
        showSessionEnded();
    }

    private void showSessionEnded() {
        removeAll();
        quantityFields.clear();
        add(new H2("Thank you for dining with us!"));
        add(new Paragraph("This dining session has ended, so no more orders can be placed."));
    }
}
