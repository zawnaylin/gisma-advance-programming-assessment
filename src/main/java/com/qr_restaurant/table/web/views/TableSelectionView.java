package com.qr_restaurant.table.web.views;

import com.qr_restaurant.table.application.entities.Table;
import com.qr_restaurant.table.application.enums.TableStatus;
import com.qr_restaurant.table.application.use_cases.commands.SelectTableCommand;
import com.qr_restaurant.table.application.use_cases.queries.GetQRForTableQuery;
import com.qr_restaurant.table.application.use_cases.queries.ShowTablesQuery;
import com.qr_restaurant.table.web.QrCodeImageFactory;
import com.qr_restaurant.table.web.TableStatusBroadcaster;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
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
import com.vaadin.flow.shared.Registration;
import org.springframework.dao.OptimisticLockingFailureException;

@Route("")
@PageTitle("Select a Table")
public class TableSelectionView extends VerticalLayout {

    private final ShowTablesQuery showTablesQuery;
    private final SelectTableCommand selectTableCommand;
    private final GetQRForTableQuery getQRForTableQuery;
    private final TableStatusBroadcaster tableStatusBroadcaster;

    private final Grid<Table> grid = new Grid<>();
    private final VerticalLayout gridPanel = new VerticalLayout();
    private final VerticalLayout qrPanel = new VerticalLayout();
    private Registration statusUpdates;

    public TableSelectionView(ShowTablesQuery showTablesQuery, SelectTableCommand selectTableCommand,
                               GetQRForTableQuery getQRForTableQuery, TableStatusBroadcaster tableStatusBroadcaster) {
        this.showTablesQuery = showTablesQuery;
        this.selectTableCommand = selectTableCommand;
        this.getQRForTableQuery = getQRForTableQuery;
        this.tableStatusBroadcaster = tableStatusBroadcaster;

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
        try {
            selectTableCommand.execute(table.getId());
        } catch (OptimisticLockingFailureException | IllegalStateException e) {
            // Someone else took this table after the grid was loaded.
            Notification.show("Table " + table.getId().value() + " was just taken. Please choose another table.");
            refresh();
            return;
        }

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

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        var ui = attachEvent.getUI();
        // Events arrive on whichever request thread changed the table. ui.access() takes this UI's
        // session lock before touching the grid, and push sends the change over long polling.
        statusUpdates = tableStatusBroadcaster.register(event -> ui.access(this::refresh));
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        statusUpdates.remove();
        statusUpdates = null;
        super.onDetach(detachEvent);
    }

    private void refresh() {
        grid.setItems(showTablesQuery.query());
    }
}
