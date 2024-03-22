package com.andreidodu.europealibrary.util;

import com.andreidodu.europealibrary.dto.BookCodesDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class EpubUtil {

    public static final String EPUB_FILE_EXTENSION = "epub";
    private final RegularExpressionUtil regularExpressionUtil;
    private final FileUtil fileUtil;

    public Optional<Book> retrieveBook(String filename) {
        if (!EPUB_FILE_EXTENSION.equals(fileUtil.getExtension(filename))) {
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

    public BookCodesDTO<Optional<String>, Optional<String>> extractISBN(Book book) {
        BookCodesDTO<Optional<String>, Optional<String>> result = new BookCodesDTO<>(Optional.empty(), Optional.empty());
        // TODO can be optimized -> usually SBN/ISBN is on the first 3-4 pages of the ebook or last 4 pages
        int i = 0;
        for (Resource content : book.getContents())
            try {
                String contentString = new String(content.getData());

                searchAndStoreISBN(result, contentString);
                searchAndStoreSBN(result, contentString);

                if (result.getIsbn13().isPresent() && result.getIsbn10().isPresent()) {
                    return result;
                }
                if (i++ > 5) {
                    return result;
                }
            } catch (IOException e) {
                log.error("failed to extract ISBN/SBN");
            }

        return result;
    }

    private void searchAndStoreSBN(BookCodesDTO<Optional<String>, Optional<String>> result, String contentString) {
        if (result.getIsbn10().isEmpty()) {
            result.setIsbn10(this.regularExpressionUtil.extractISBN10(contentString));
        }
    }

    private void searchAndStoreISBN(BookCodesDTO<Optional<String>, Optional<String>> result, String contentString) {
        if (result.getIsbn13().isEmpty()) {
            result.setIsbn13(this.regularExpressionUtil.extractISBN13(contentString));
        }
    }


    public String getEpubFileExtension() {
        return EPUB_FILE_EXTENSION;
    }

    public boolean isEpub(String fullPath) {
        return !(new File(fullPath).isDirectory()) && EPUB_FILE_EXTENSION.equals(this.fileUtil.getExtension(fullPath));
    }
}
