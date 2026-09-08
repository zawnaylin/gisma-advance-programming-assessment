package com.qr_restaurant.table.web.converters;

import com.qr_restaurant.table.application.use_cases.dtos.QRDto;
import com.qr_restaurant.table.web.dtos.responses.GetQRResponseDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
public class GetQRResponseDtoConverter implements Converter<QRDto, GetQRResponseDto> {
    @Override
    public GetQRResponseDto convert(QRDto source) {
        return new GetQRResponseDto(
                source.url(),
                source.tableId().value(),
                source.diningSessionId().value()
        );
    }
}
