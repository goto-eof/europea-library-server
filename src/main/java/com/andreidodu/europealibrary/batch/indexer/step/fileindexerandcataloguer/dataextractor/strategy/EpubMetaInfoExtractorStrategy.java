package com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer.dataextractor.strategy;

import com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer.dataextractor.MetaInfoExtractorStrategy;
import com.andreidodu.europealibrary.dto.BookCodesDTO;
import com.andreidodu.europealibrary.model.BookInfo;
import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.util.EpubUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Identifier;
import nl.siegmann.epublib.domain.Metadata;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class EpubMetaInfoExtractorStrategy implements MetaInfoExtractorStrategy {
    final private static String STRATEGY_NAME = "epub-meta-info-extractor-strategy";
    private final EpubUtil epubUtil;
    private final DataExtractorStrategyUtil dataExtractorStrategyUtil;


    @Override
    public boolean accept(String filename) {
        return epubUtil.isEpub(filename);
    }

    @Override
    public Optional<FileMetaInfo> extract(String filename) {
        return epubUtil.retrieveBook(filename)
                .map(book -> {
                    if (book.getMetadata().getFirstTitle().trim().isEmpty()) {
                        return null;
                    }
                    return manageCaseBookTitleNotEmpty(book, filename);
                });
    }

    private FileMetaInfo manageCaseBookTitleNotEmpty(Book book, String fullPath) {
        log.info("gathering information from ebook {}", fullPath);
        FileMetaInfo fileMetaInfo = new FileMetaInfo();

        Metadata metadata = book.getMetadata();
        fileMetaInfo.setTitle(metadata.getFirstTitle());
        fileMetaInfo.setDescription(getFirst(metadata.getDescriptions()));

        BookInfo bookInfo = buildBookInfo(book, fileMetaInfo, metadata);

        fileMetaInfo.setBookInfo(bookInfo);
        return fileMetaInfo;
    }

    private BookInfo buildBookInfo(Book book, FileMetaInfo fileMetaInfo, Metadata metadata) {
        BookInfo bookInfo = new BookInfo();
        bookInfo.setFileMetaInfo(fileMetaInfo);
        Optional.ofNullable(metadata.getLanguage())
                .ifPresent(language ->
                        bookInfo.setLanguage(language.substring(0, Math.min(language.length(), 10))));
        bookInfo.setNumberOfPages(book.getContents().size());
        bookInfo.setAuthors(String.join(", ", metadata.getAuthors()
                .stream()
                .map(author -> author.getFirstname() + " " + author.getLastname())
                .toList()));
        BookCodesDTO<Optional<String>, Optional<String>> bookCodes = this.epubUtil.retrieveISBN(book);
        dataExtractorStrategyUtil.setISBN13(bookCodes, bookInfo);
        dataExtractorStrategyUtil.setISBN10(bookCodes, bookInfo);
        bookInfo.setPublisher(String.join(", ", metadata.getPublishers()));
        return bookInfo;
    }

    private String getFirst(List<String> list) {
        if (list != null && !list.isEmpty()) {
            list.getFirst();
        }
        return null;
    }
}
