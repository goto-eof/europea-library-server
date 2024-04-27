package com.andreidodu.europealibrary.controller;

import com.andreidodu.europealibrary.dto.DownloadDTO;
import com.andreidodu.europealibrary.service.QRCodeService;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/v1/qrcode")
@RequiredArgsConstructor
public class QRCodeController {
    private final QRCodeService qrCodeService;
    @Value("${com.andreidodu.europea-library.client.url}")
    private String clientUrl;
    @Value("${com.andreidodu.europea-library.client.view-book-info-endpoint}")
    private String viewBookInfoEndpoint;

    @GetMapping(path = "/get/{fileSystemItemId}")
    public ResponseEntity<InputStreamResource> viewBookInfoQRCode(@PathVariable Long fileSystemItemId) throws IOException, WriterException {
        final String viewBookInfoUrl = clientUrl + viewBookInfoEndpoint + "/" + fileSystemItemId;
        DownloadDTO download = this.qrCodeService.generateOrLoadQRCode(viewBookInfoUrl, fileSystemItemId);
        return ResponseEntity.ok()
                .contentLength(download.getFileSize())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(download.getInputStreamResource());
    }
}
