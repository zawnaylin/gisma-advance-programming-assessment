package com.qr_restaurant.table.web.views;

import com.qr_restaurant.table.application.entities.Table;
import com.qr_restaurant.table.application.enums.TableStatus;
import com.qr_restaurant.table.application.use_cases.commands.SelectTableCommand;
import com.qr_restaurant.table.application.use_cases.queries.GetQRForTableQuery;
import com.qr_restaurant.table.application.use_cases.queries.ShowTablesQuery;
import com.qr_restaurant.table.web.QrCodeImageFactory;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("")
@PageTitle("Select a Table")
public class TableSelectionView extends VerticalLayout {

    private final ShowTablesQuery showTablesQuery;
    private final SelectTableCommand selectTableCommand;
    private final GetQRForTableQuery getQRForTableQuery;

    private final Grid<Table> grid = new Grid<>();
    private final VerticalLayout gridPanel = new VerticalLayout();
    private final VerticalLayout qrPanel = new VerticalLayout();

    public TableSelectionView(ShowTablesQuery showTablesQuery, SelectTableCommand selectTableCommand,
                               GetQRForTableQuery getQRForTableQuery) {
        this.showTablesQuery = showTablesQuery;
        this.selectTableCommand = selectTableCommand;
        this.getQRForTableQuery = getQRForTableQuery;

        add(new H2("QR Restaurant - Choose a Table"), new Anchor("staff/tables", "Staff view"));

        grid.addColumn(t -> t.getId().value()).setHeader("Table");
        grid.addColumn(Table::getCapacity).setHeader("Capacity");
        grid.addColumn(t -> t.getStatus().name()).setHeader("Status");
        grid.addComponentColumn(this::selectButton).setHeader("Action");
        gridPanel.add(grid);

        qrPanel.setVisible(false);

        add(gridPanel, qrPanel);

        refresh();
    }

    private Button selectButton(Table table) {
        var button = new Button("Select", e -> selectTable(table));
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.setEnabled(table.getStatus() == TableStatus.AVAILABLE);
        return button;
    }

    private void selectTable(Table table) {
        selectTableCommand.execute(table.getId());
        var qr = getQRForTableQuery.query(table.getId());

        if (qr == null) {
            Notification.show("Could not start a session for this table");
            refresh();
            return;
        }

        showQrPanel(table, qr.generateUrl());
    }

    private void showQrPanel(Table table, String url) {
        qrPanel.removeAll();
        qrPanel.add(new H3("Table " + table.getId().value() + " - scan to view the menu"));
        qrPanel.add(QrCodeImageFactory.create(url, 260));
        qrPanel.add(new Span(url));
        qrPanel.add(new Anchor(url, "Or open the menu in this browser"));

        var back = new Button("Back to tables", e -> {
            qrPanel.setVisible(false);
            gridPanel.setVisible(true);
            refresh();
        });
        qrPanel.add(back);

        gridPanel.setVisible(false);
        qrPanel.setVisible(true);
    }

    private void refresh() {
        grid.setItems(showTablesQuery.query());
    }
}
