package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.dto.DownloadDTO;
import com.andreidodu.europealibrary.service.QRCodeService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class QRCodeServiceImpl implements QRCodeService {
    public static final String QRCODE_FILE_EXTENSION_PNG = ".png";
    @Value("${com.andreidodu.europea-library.qr-code-path}")
    private String qrCodePath;


    public static final String DEFAULT_CHARSET = "UTF-8";
    public static final int HEIGHT = 250;
    public static final int WIDTH = 250;

    @Override
    public DownloadDTO generateOrLoadQRCode(String data, Long identifier)
            throws WriterException, IOException {
        new File(qrCodePath).mkdirs();
        final String filename = qrCodePath + "/" + identifier + QRCODE_FILE_EXTENSION_PNG;

        if (new File(filename).exists()) {
            return fileToDownloadDTO(filename);
        }

        BitMatrix matrix = new MultiFormatWriter().encode(
                new String(data.getBytes(DEFAULT_CHARSET), DEFAULT_CHARSET),
                BarcodeFormat.QR_CODE, WIDTH, HEIGHT);

        MatrixToImageWriter.writeToPath(
                matrix,
                filename.substring(filename.lastIndexOf('.') + 1),
                Path.of(filename));

        return fileToDownloadDTO(filename);
    }

    private static DownloadDTO fileToDownloadDTO(String filename) throws FileNotFoundException {
        DownloadDTO downloadDTO = new DownloadDTO();
        File file = new File(filename);
        FileInputStream fileInputStream = new FileInputStream(file);
        InputStreamResource inputStreamResource = new InputStreamResource(fileInputStream);
        downloadDTO.setFileSize(file.length());
        downloadDTO.setInputStreamResource(inputStreamResource);
        return downloadDTO;
    }
}
