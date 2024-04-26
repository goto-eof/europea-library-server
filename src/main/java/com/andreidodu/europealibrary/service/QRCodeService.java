package com.andreidodu.europealibrary.service;

import com.andreidodu.europealibrary.dto.DownloadDTO;
import com.google.zxing.WriterException;

import java.io.IOException;

public interface QRCodeService {
    DownloadDTO generateOrLoadQRCode(String data, Long identifier)
            throws WriterException, IOException;
}
