package com.andreidodu.europealibrary.util;

import com.andreidodu.europealibrary.dto.BookCodesDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class EpubUtil {

    public static final String EPUB_FILE_EXTENSION = "epub";
    private final RegularExpressionUtil regularExpressionUtil;

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

    public BookCodesDTO<Optional<String>, Optional<String>> retrieveISBN(Book book) {
        BookCodesDTO<Optional<String>, Optional<String>> result = new BookCodesDTO<>(Optional.empty(), Optional.empty());
        // TODO can be optimized -> usually SBN/ISBN is on the first 3-4 pages of the ebook or last 4 pages
        int i = 0;
        for (Resource content : book.getContents())
            try {
                String contentString = new String(content.getData());

                searchAndStoreISBN(result, contentString);
                searchAndStoreSBN(result, contentString);

                if (result.getIsbn().isPresent() && result.getSbn().isPresent()) {
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
        if (result.getSbn().isEmpty()) {
            result.setSbn(this.regularExpressionUtil.extractSBN(contentString));
        }
    }

    private void searchAndStoreISBN(BookCodesDTO<Optional<String>, Optional<String>> result, String contentString) {
        if (result.getIsbn().isEmpty()) {
            result.setIsbn(this.regularExpressionUtil.extractISBN(contentString));
        }
    }
}
