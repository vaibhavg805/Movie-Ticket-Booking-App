package com.vaibhav.booking.system.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
public class QRCodeUtil {

    private static final String ISSUER = "MovieBookingApp"; // Or inject via config
    private static final int QR_WIDTH = 200;
    private static final int QR_HEIGHT = 200;

    public static String generateBase64QRCode(String username, String secret) {
        try {
            String otpAuthUrl = generateOtpAuthUrl(username, secret);
            BitMatrix bitMatrix = new QRCodeWriter()
                    .encode(otpAuthUrl, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception ex) {
            log.error("Failed to generate QR Code for user: {}", username, ex);
            throw new RuntimeException("Unable to generate QR code", ex);
        }
    }

    private static String generateOtpAuthUrl(String username, String secret) {
        return String.format(
                "otpauth://totp/%s?secret=%s&issuer=%s",
                URLEncoder.encode(username, StandardCharsets.UTF_8),
                URLEncoder.encode(secret, StandardCharsets.UTF_8),
                URLEncoder.encode(ISSUER, StandardCharsets.UTF_8)
        );
    }
}

