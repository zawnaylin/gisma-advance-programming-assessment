package com.qr_restaurant.table.web.controllers;

import com.qr_restaurant.table.application.entities.Table;
import com.qr_restaurant.table.application.use_cases.commands.SelectTableCommand;
import com.qr_restaurant.table.application.use_cases.dtos.QRDto;
import com.qr_restaurant.table.application.use_cases.queries.GetQRForTableQuery;
import com.qr_restaurant.table.application.use_cases.queries.ShowTablesQuery;
import com.qr_restaurant.table.application.vo.TableId;
import com.qr_restaurant.table.web.dtos.responses.GetQRResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
@RequiredArgsConstructor
public class TableController {

    private final ShowTablesQuery showTablesQuery;
    private final SelectTableCommand selectTableCommand;
    private final GetQRForTableQuery getQRForTableQuery;

    private final Converter<QRDto, GetQRResponseDto> getQRResponseDtoConverter;

    @GetMapping
    public List<Table> getTables() {
        return showTablesQuery.query();
    }

    @PostMapping("/{id}/select")
    public void selectTable(@PathVariable String id) {
        selectTableCommand.execute(new TableId(id));
    }

    @GetMapping("/{tableId}/qr")
    public ResponseEntity<GetQRResponseDto> getQR(@PathVariable String tableId) {

        var result = getQRForTableQuery.query(new TableId(tableId));

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        var resp = getQRResponseDtoConverter.convert(result);

        return ResponseEntity.ok(resp);
    }
}
