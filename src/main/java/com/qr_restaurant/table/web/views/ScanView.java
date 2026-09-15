package com.qr_restaurant.table.web.views;

import com.qr_restaurant.table.application.use_cases.commands.ScanQrCommand;
import com.qr_restaurant.table.application.vo.DiningSessionId;
import com.qr_restaurant.table.application.vo.TableId;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("scan")
@PageTitle("Scanning...")
public class ScanView extends VerticalLayout implements BeforeEnterObserver {

    private final ScanQrCommand scanQrCommand;

    public ScanView(ScanQrCommand scanQrCommand) {
        this.scanQrCommand = scanQrCommand;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        var params = event.getLocation().getQueryParameters().getParameters();
        var tableId = params.getOrDefault("table", java.util.List.of()).stream().findFirst().orElse(null);
        var sessionId = params.getOrDefault("session", java.util.List.of()).stream().findFirst().orElse(null);

        if (tableId == null || sessionId == null) {
            showError("This QR code is missing information.");
            return;
        }

        try {
            var session = scanQrCommand.execute(new TableId(tableId), new DiningSessionId(sessionId));
            event.forwardTo("menu/" + session.getId().value());
        } catch (RuntimeException e) {
            showError("This QR code is no longer valid: " + e.getMessage());
        }
    }

    private void showError(String message) {
        add(new H2("Cannot open menu"), new Paragraph(message), new Anchor("", "Back to tables"));
    }
}
