package com.qr_restaurant.order.web.controllers;

import com.qr_restaurant.menu.vo.MenuItemId;
import com.qr_restaurant.order.entities.Order;
import com.qr_restaurant.order.enums.OrderStatus;
import com.qr_restaurant.order.use_cases.commands.CancelOrderCommand;
import com.qr_restaurant.order.use_cases.commands.ConfirmOrderCommand;
import com.qr_restaurant.order.use_cases.commands.MarkOrderReadyCommand;
import com.qr_restaurant.order.use_cases.commands.PlaceOrderCommand;
import com.qr_restaurant.order.use_cases.commands.ServeOrderCommand;
import com.qr_restaurant.order.use_cases.commands.dtos.CancelOrderDto;
import com.qr_restaurant.order.use_cases.commands.dtos.PlaceOrderDto;
import com.qr_restaurant.order.use_cases.queries.TrackOrderStatusQuery;
import com.qr_restaurant.order.use_cases.queries.ViewIncomingOrdersQuery;
import com.qr_restaurant.order.vo.OrderId;
import com.qr_restaurant.order.web.dtos.requests.CancelOrderRequestDto;
import com.qr_restaurant.order.web.dtos.requests.PlaceOrderRequestDto;
import com.qr_restaurant.order.web.dtos.responses.GetOrderResponseDto;
import com.qr_restaurant.order.web.dtos.responses.GetOrdersResponseDto;
import com.qr_restaurant.order.web.dtos.responses.PlaceOrderResponseDto;
import com.qr_restaurant.order.web.dtos.responses.common.OrderDto;
import com.qr_restaurant.table.application.vo.DiningSessionId;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final PlaceOrderCommand placeOrderCommand;
    private final ConfirmOrderCommand confirmOrderCommand;
    private final MarkOrderReadyCommand markOrderReadyCommand;
    private final ServeOrderCommand serveOrderCommand;
    private final CancelOrderCommand cancelOrderCommand;
    private final TrackOrderStatusQuery trackOrderStatusQuery;
    private final ViewIncomingOrdersQuery viewIncomingOrdersQuery;

    private final Converter<Order, OrderDto> orderDtoConverter;

    @PostMapping
    public ResponseEntity<PlaceOrderResponseDto> placeOrder(@RequestBody PlaceOrderRequestDto request) {
        var items = request.items().stream()
                .map(line -> new PlaceOrderDto.Item(new MenuItemId(line.menuItemId()), line.quantity()))
                .toList();

        var orderId = placeOrderCommand.execute(new PlaceOrderDto(new DiningSessionId(request.diningSessionId()), items));

        return ResponseEntity.ok(new PlaceOrderResponseDto(orderId.value()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetOrderResponseDto> getOrder(@PathVariable String id) {
        var order = trackOrderStatusQuery.query(new OrderId(id));
        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new GetOrderResponseDto(orderDtoConverter.convert(order)));
    }

    @GetMapping
    public ResponseEntity<GetOrdersResponseDto> getOrders(@RequestParam(required = false) OrderStatus status) {
        var body = viewIncomingOrdersQuery.query(status).stream().map(orderDtoConverter::convert).toList();
        return ResponseEntity.ok(new GetOrdersResponseDto(body));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<Void> confirm(@PathVariable String id) {
        confirmOrderCommand.execute(new OrderId(id));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/ready")
    public ResponseEntity<Void> markReady(@PathVariable String id) {
        markOrderReadyCommand.execute(new OrderId(id));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/serve")
    public ResponseEntity<Void> serve(@PathVariable String id) {
        serveOrderCommand.execute(new OrderId(id));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable String id, @RequestBody CancelOrderRequestDto request) {
        cancelOrderCommand.execute(new OrderId(id), new CancelOrderDto(request.cancelledBy(), request.reason()));
        return ResponseEntity.ok().build();
    }
}
