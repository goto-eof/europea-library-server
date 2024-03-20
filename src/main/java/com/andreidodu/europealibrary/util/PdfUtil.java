package com.andreidodu.europealibrary.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@RequiredArgsConstructor
public class PdfUtil {
    private static final String PDF_FILE_EXTENSION = "pdf";
    private final FileUtil fileUtil;

    public boolean isPdf(String fullPath) {
        return !(new File(fullPath).isDirectory()) && PDF_FILE_EXTENSION.equals(this.fileUtil.getExtension(fullPath));
    }
}
