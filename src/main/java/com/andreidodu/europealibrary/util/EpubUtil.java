package com.andreidodu.europealibrary.util;

import lombok.extern.slf4j.Slf4j;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
public class EpubUtil {

    public static final String EPUB_FILE_EXTENSION = "epub";

    public Optional<Book> retrieveBook(String filename) {
        if (!EPUB_FILE_EXTENSION.equals(FilenameUtils.getExtension(filename))) {
            return Optional.empty();
        }
        try {
            EpubReader epubReader = new EpubReader();
            return Optional.of(epubReader.readEpub(new FileInputStream(filename)));
        } catch (IOException e) {
            log.error("failed to read epub: {}", filename);
        }
        return Optional.empty();
    }
}
