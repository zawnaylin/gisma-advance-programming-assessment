package com.qr_restaurant.table.web.views;

import com.qr_restaurant.table.application.entities.Table;
import com.qr_restaurant.table.application.enums.TableStatus;
import com.qr_restaurant.table.application.use_cases.commands.RegenerateSessionCommand;
import com.qr_restaurant.table.application.use_cases.queries.GetQRForTableQuery;
import com.qr_restaurant.table.application.use_cases.queries.ShowTablesQuery;
import com.qr_restaurant.table.web.QrCodeImageFactory;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("staff/tables")
@PageTitle("Staff - Tables")
public class StaffTablesView extends VerticalLayout {

    private final ShowTablesQuery showTablesQuery;
    private final RegenerateSessionCommand regenerateSessionCommand;
    private final GetQRForTableQuery getQRForTableQuery;

    private final Grid<Table> grid = new Grid<>();
    private final VerticalLayout qrPanel = new VerticalLayout();

    public StaffTablesView(ShowTablesQuery showTablesQuery, RegenerateSessionCommand regenerateSessionCommand,
                            GetQRForTableQuery getQRForTableQuery) {
        this.showTablesQuery = showTablesQuery;
        this.regenerateSessionCommand = regenerateSessionCommand;
        this.getQRForTableQuery = getQRForTableQuery;

        add(new H2("Staff - All Tables"), new Anchor("", "Customer view"), new Anchor("kitchen", "Kitchen view"));

        grid.addColumn(t -> t.getId().value()).setHeader("Table");
        grid.addColumn(Table::getCapacity).setHeader("Capacity");
        grid.addColumn(t -> t.getStatus().name()).setHeader("Status");
        grid.addComponentColumn(this::regenerateButton).setHeader("Actions");
        add(grid);

        qrPanel.setVisible(false);
        add(qrPanel);

        refresh();
    }

    private Button regenerateButton(Table table) {
        var button = new Button("Regenerate session", e -> regenerate(table));
        button.setEnabled(table.getStatus() == TableStatus.OCCUPIED);
        return button;
    }

    private void regenerate(Table table) {
        regenerateSessionCommand.execute(table.getId());

        var qr = getQRForTableQuery.query(table.getId());
        if (qr == null) {
            Notification.show("Could not regenerate session for this table");
            return;
        }

        qrPanel.removeAll();
        qrPanel.add(new H3("New QR for table " + table.getId().value()));
        qrPanel.add(QrCodeImageFactory.create(qr.generateUrl(), 220));
        qrPanel.add(new Span(qr.generateUrl()));
        qrPanel.setVisible(true);

        Notification.show("Session regenerated for table " + table.getId().value() + " - old QR is now invalid");
        refresh();
    }

    private void refresh() {
        grid.setItems(showTablesQuery.query());
    }
}
