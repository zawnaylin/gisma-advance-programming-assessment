package com.qr_restaurant.table.web.converters;

import com.qr_restaurant.table.application.entities.QR;
import com.qr_restaurant.table.web.dtos.responses.common.QRDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
public class QRResponseDtoConverter implements Converter<QR, QRDto> {
    @Override
    public QRDto convert(QR source) {
        return new QRDto(source.generateUrl());
    }
}
