package com.andreidodu.europealibrary.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@Slf4j
@Component
public class FileUtil {
    public String fileToSha256(File file) {
        try (InputStream is = Files.newInputStream(file.toPath())) {
            return org.apache.commons.codec.digest.DigestUtils.sha256Hex(is);
        } catch (IOException e) {
            return null;
        }

    }
}
