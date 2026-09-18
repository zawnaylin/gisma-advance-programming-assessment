package com.qr_restaurant;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.shared.ui.Transport;

// Server push over a WebSocket (client-to-server messages go over XHR); falls back to long polling
// if a WebSocket can't be opened. Plain long polling holds one of the browser's ~6 HTTP/1.1
// connections per open tab, which froze the browser once a few app tabs were open.
@Push(transport = Transport.WEBSOCKET_XHR)
public class AppShell implements AppShellConfigurator {
}
