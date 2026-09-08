package com.qr_restaurant.table.web.controllers;

import com.qr_restaurant.table.application.entities.QR;
import com.qr_restaurant.table.application.entities.Table;
import com.qr_restaurant.table.application.use_cases.commands.SelectTableCommand;
import com.qr_restaurant.table.application.use_cases.queries.GetQRForTableQuery;
import com.qr_restaurant.table.application.use_cases.queries.ShowTablesQuery;
import com.qr_restaurant.table.application.vo.TableId;
import com.qr_restaurant.table.web.dtos.responses.GetQRResponseDto;
import com.qr_restaurant.table.web.dtos.responses.GetTablesResponseDto;
import com.qr_restaurant.table.web.dtos.responses.SelectTableResponseDto;
import com.qr_restaurant.table.web.dtos.responses.common.QRDto;
import com.qr_restaurant.table.web.dtos.responses.common.TableDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tables")
@RequiredArgsConstructor
public class TableController {

    private final ShowTablesQuery showTablesQuery;
    private final SelectTableCommand selectTableCommand;
    private final GetQRForTableQuery getQRForTableQuery;

    private final Converter<QR, QRDto> qrDtoConverter;
    private final Converter<Table, TableDto> tableDtoConverter;

    @GetMapping
    public ResponseEntity<GetTablesResponseDto> getTables() {

        var tables = showTablesQuery.query();

        var body = tables.stream().map(tableDtoConverter::convert).toList();
        var resp = new GetTablesResponseDto(body);

        return ResponseEntity.ok(resp);
    }

    @PostMapping("/{id}/select")
    public ResponseEntity<SelectTableResponseDto> selectTable(@PathVariable String id) {
        selectTableCommand.execute(new TableId(id));

        var qr = getQRForTableQuery.query(new TableId(id));

        var body = qrDtoConverter.convert(qr);
        var resp = new SelectTableResponseDto(body);

        return ResponseEntity.ok(resp);
    }

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
