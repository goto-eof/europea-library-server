package com.andreidodu.europealibrary.resource;

import com.google.zxing.WriterException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@RequestMapping("/api/v1/qrcode")
public interface QRCodeResource {
    @GetMapping(path = "/get/{fileSystemItemId}")
    ResponseEntity<InputStreamResource> viewBookInfoQRCode(@PathVariable Long fileSystemItemId) throws IOException, WriterException;
}
