package com.qr_restaurant.table.web;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public final class QrCodeImageFactory {

    private QrCodeImageFactory() {
    }

    public static Image create(String text, int sizePx) {
        BitMatrix matrix;
        try {
            matrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, sizePx, sizePx);
        } catch (WriterException e) {
            throw new IllegalStateException("Could not generate QR code", e);
        }

        var handler = DownloadHandler.fromInputStream(event -> {
            var out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", out);
            var bytes = out.toByteArray();
            return new DownloadResponse(new ByteArrayInputStream(bytes), "qr-code.png", "image/png", bytes.length);
        });

        var image = new Image(handler, "QR code");
        image.setWidth(sizePx + "px");
        image.setHeight(sizePx + "px");
        return image;
    }
}
