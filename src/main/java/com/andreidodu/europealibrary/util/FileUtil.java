package com.andreidodu.europealibrary.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Optional;

@Slf4j
@Component
public class FileUtil {

    public static final int EXTENSION_LENGTH_MAX = 100;

    public Optional<String> fileToSha256(File file) {
        try (InputStream is = Files.newInputStream(file.toPath())) {
            return Optional.of(org.apache.commons.codec.digest.DigestUtils.sha256Hex(is));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public Optional<String> fileNameToHash(String filename) {
        return this.fileToSha256(new File(filename));
    }

    public String calculateParentName(String basePath) {
        return new File(basePath).getName();
    }

    public String calculateFileName(String basePath) {
        return new File(basePath).getName();
    }


    public String calculateParentBasePath(String basePath) {
        return new File(basePath).getParentFile().getAbsolutePath();
    }

    public String getExtension(String filename) {
        String extension = FilenameUtils.getExtension(filename).trim();
        if (extension.length() > EXTENSION_LENGTH_MAX) {
            return extension.substring(0, EXTENSION_LENGTH_MAX);
        }
        return extension;
    }

    public boolean isDirectory(String filename) {
        return new File(filename).isDirectory();
    }
}
