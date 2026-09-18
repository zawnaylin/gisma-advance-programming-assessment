package com.qr_restaurant.table.web.converters;

import com.qr_restaurant.table.application.entities.Table;
import com.qr_restaurant.table.web.dtos.responses.common.TableDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TableDtoConverter implements Converter<Table, TableDto> {
    @Override
    public TableDto convert(Table source) {
        return new TableDto(
                source.getId().value(),
                source.getCapacity(),
                source.getStatus().name()
        );
    }
}
