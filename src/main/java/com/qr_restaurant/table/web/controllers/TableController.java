package com.qr_restaurant.table.web.controllers;

import com.qr_restaurant.table.application.entities.QR;
import com.qr_restaurant.table.application.entities.Table;
import com.qr_restaurant.table.application.use_cases.commands.ManageTableCommand;
import com.qr_restaurant.table.application.use_cases.commands.SelectTableCommand;
import com.qr_restaurant.table.application.use_cases.queries.GetQRForTableQuery;
import com.qr_restaurant.table.application.use_cases.queries.GetTableQuery;
import com.qr_restaurant.table.application.use_cases.queries.ShowTablesQuery;
import com.qr_restaurant.table.application.vo.TableId;
import com.qr_restaurant.table.web.dtos.requests.TableRequestDto;
import com.qr_restaurant.table.web.dtos.responses.GetQRResponseDto;
import com.qr_restaurant.table.web.dtos.responses.GetTablesResponseDto;
import com.qr_restaurant.table.web.dtos.responses.SelectTableResponseDto;
import com.qr_restaurant.table.web.dtos.responses.TableResponseDto;
import com.qr_restaurant.table.web.dtos.responses.common.QRDto;
import com.qr_restaurant.table.web.dtos.responses.common.TableDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * REST API for tables: CRUD plus the two steps of the dining flow that customers reach
 * over HTTP. Ending a session and marking a table cleaned are only in the web UI.
 * See docs/rest-api.md.
 */
@RestController
@RequestMapping("/api/tables")
@RequiredArgsConstructor
public class TableController {

    private final ShowTablesQuery showTablesQuery;
    private final GetTableQuery getTableQuery;
    private final SelectTableCommand selectTableCommand;
    private final ManageTableCommand manageTableCommand;
    private final GetQRForTableQuery getQRForTableQuery;

    private final Converter<QR, QRDto> qrDtoConverter;
    private final Converter<Table, TableDto> tableDtoConverter;

    /**
     * @return all tables with their current status
     */
    @GetMapping
    public ResponseEntity<GetTablesResponseDto> getTables() {

        var tables = showTablesQuery.query();

        var body = tables.stream().map(tableDtoConverter::convert).toList();
        var resp = new GetTablesResponseDto(body);

        return ResponseEntity.ok(resp);
    }

    /**
     * @return the table, or 404 if there is none with that id
     */
    @GetMapping("/{id}")
    public ResponseEntity<TableResponseDto> getTable(@PathVariable String id) {
        var table = getTableQuery.query(new TableId(id));
        if (table == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new TableResponseDto(tableDtoConverter.convert(table)));
    }

    /**
     * Adds a table; the body's id is optional and generated when absent.
     *
     * @return 201 with a Location header, or 400 for an invalid body and 409 if the id is taken
     */
    @PostMapping
    public ResponseEntity<TableResponseDto> createTable(@Valid @RequestBody TableRequestDto request) {
        var id = manageTableCommand.create(
                request.id() == null ? null : new TableId(request.id()),
                request.capacity());

        var body = tableDtoConverter.convert(getTableQuery.query(id));
        return ResponseEntity.created(URI.create("/api/tables/" + id.value())).body(new TableResponseDto(body));
    }

    /**
     * Changes a table's capacity. Its status is derived from its dining session and
     * cannot be set here.
     *
     * @return 200 with the updated resource, 400 for an invalid body, or 404 if it doesn't exist
     */
    @PutMapping("/{id}")
    public ResponseEntity<TableResponseDto> updateTable(@PathVariable String id,
                                                        @Valid @RequestBody TableRequestDto request) {
        var tableId = new TableId(id);
        manageTableCommand.updateCapacity(tableId, request.capacity());

        var body = tableDtoConverter.convert(getTableQuery.query(tableId));
        return ResponseEntity.ok(new TableResponseDto(body));
    }

    /**
     * Removes a table.
     *
     * @return 204, 404 if it doesn't exist, or 409 while guests are seated or once the
     *         table has dining sessions in its history
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable String id) {
        manageTableCommand.delete(new TableId(id));
        return ResponseEntity.noContent().build();
    }

    /**
     * Seats guests at a free table, which starts a dining session.
     *
     * @return 200 with the QR code URL for that session, 404 if the table doesn't exist,
     *         or 409 if it is not available
     */
    @PostMapping("/{id}/select")
    public ResponseEntity<SelectTableResponseDto> selectTable(@PathVariable String id) {
        selectTableCommand.execute(new TableId(id));

        var qr = getQRForTableQuery.query(new TableId(id));

        var body = qrDtoConverter.convert(qr);
        var resp = new SelectTableResponseDto(body);

        return ResponseEntity.ok(resp);
    }

    /**
     * The QR code URL of the table's current session, e.g. to print it again.
     *
     * @return 200 with the URL, or 404 if the table doesn't exist or has no session
     *         that accepts orders
     */
    @GetMapping("/{tableId}/qr")
    public ResponseEntity<GetQRResponseDto> getQR(@PathVariable String tableId) {

        var result = getQRForTableQuery.query(new TableId(tableId));

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        var body = qrDtoConverter.convert(result);
        var resp = new GetQRResponseDto(body);

        return ResponseEntity.ok(resp);


    }
}
