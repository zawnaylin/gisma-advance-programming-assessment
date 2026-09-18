package com.qr_restaurant.table.web.views;

import com.qr_restaurant.table.application.entities.Table;
import com.qr_restaurant.table.application.enums.TableStatus;
import com.qr_restaurant.table.application.use_cases.commands.EndDiningSessionCommand;
import com.qr_restaurant.table.application.use_cases.commands.MarkTableCleanedCommand;
import com.qr_restaurant.table.application.use_cases.queries.GetQRForTableQuery;
import com.qr_restaurant.table.application.use_cases.queries.ShowTablesQuery;
import com.qr_restaurant.table.web.QrCodeImageFactory;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.dao.OptimisticLockingFailureException;

@Route("staff/tables")
@PageTitle("Staff - Tables")
public class StaffTablesView extends VerticalLayout {

    private final ShowTablesQuery showTablesQuery;
    private final GetQRForTableQuery getQRForTableQuery;
    private final MarkTableCleanedCommand markTableCleanedCommand;
    private final EndDiningSessionCommand endDiningSessionCommand;

    private final Grid<Table> grid = new Grid<>();
    private final VerticalLayout qrPanel = new VerticalLayout();

    public StaffTablesView(ShowTablesQuery showTablesQuery, GetQRForTableQuery getQRForTableQuery,
                           MarkTableCleanedCommand markTableCleanedCommand,
                           EndDiningSessionCommand endDiningSessionCommand) {
        this.showTablesQuery = showTablesQuery;
        this.getQRForTableQuery = getQRForTableQuery;
        this.markTableCleanedCommand = markTableCleanedCommand;
        this.endDiningSessionCommand = endDiningSessionCommand;

        add(new H2("Staff - All Tables"), new Anchor("", "Customer view"), new Anchor("kitchen", "Kitchen view"));

        grid.addColumn(t -> t.getId().value()).setHeader("Table");
        grid.addColumn(Table::getCapacity).setHeader("Capacity");
        grid.addColumn(t -> t.getStatus().name()).setHeader("Status");
        grid.addComponentColumn(this::actions).setHeader("Actions");
        add(grid);

        qrPanel.setVisible(false);
        add(qrPanel);

        refresh();
    }

    private HorizontalLayout actions(Table table) {
        // Re-shows the current session's QR (e.g. the printed one was lost); existing orders are kept.
        var showQr = new Button("Show QR", e -> showQr(table));
        showQr.setEnabled(table.getStatus() == TableStatus.OCCUPIED);

        // Admin control, e.g. the guests left without pressing "End order".
        var endSession = new Button("End session", e -> confirmEndSession(table));
        endSession.addThemeVariants(ButtonVariant.LUMO_ERROR);
        endSession.setEnabled(table.getStatus() == TableStatus.OCCUPIED);

        var markCleaned = new Button("Mark cleaned", e -> markCleaned(table));
        markCleaned.setEnabled(table.getStatus() == TableStatus.WAITING_FOR_CLEANING);

        return new HorizontalLayout(showQr, endSession, markCleaned);
    }

    private void confirmEndSession(Table table) {
        // Always confirmed: the table module can't count the session's open orders
        // without depending on the order module, which already depends on this one.
        var dialog = new ConfirmDialog();
        dialog.setHeader("End dining session for table " + table.getId().value() + "?");
        dialog.setText("The table will be locked for new orders and marked as waiting for cleaning. "
                + "Orders the kitchen hasn't finished will be cancelled.");
        dialog.setCancelable(true);
        dialog.setConfirmText("End session");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(e -> endSession(table));
        dialog.open();
    }

    private void endSession(Table table) {
        try {
            endDiningSessionCommand.execute(table.getId());
            Notification.show("Dining session ended for table " + table.getId().value());
        } catch (OptimisticLockingFailureException | IllegalStateException e) {
            // The customer ended it at the same time, or the grid was out of date.
            Notification.show("Table " + table.getId().value() + " was updated by someone else");
        }
        qrPanel.setVisible(false);
        refresh();
    }

    private void showQr(Table table) {
        var qr = getQRForTableQuery.query(table.getId());
        if (qr == null) {
            Notification.show("Table " + table.getId().value() + " has no active dining session");
            refresh();
            return;
        }

        qrPanel.removeAll();
        qrPanel.add(new H3("QR for table " + table.getId().value()));
        qrPanel.add(QrCodeImageFactory.create(qr.generateUrl(), 220));
        qrPanel.add(new Span(qr.generateUrl()));
        qrPanel.setVisible(true);
    }

    private void markCleaned(Table table) {
        try {
            markTableCleanedCommand.execute(table.getId());
            Notification.show("Table " + table.getId().value() + " is available again");
        } catch (OptimisticLockingFailureException | IllegalStateException e) {
            // Another waiter already cleaned it, or the grid was out of date.
            Notification.show("Table " + table.getId().value() + " was updated by someone else");
        }
        qrPanel.setVisible(false);
        refresh();
    }

    private void refresh() {
        grid.setItems(showTablesQuery.query());
    }
}
