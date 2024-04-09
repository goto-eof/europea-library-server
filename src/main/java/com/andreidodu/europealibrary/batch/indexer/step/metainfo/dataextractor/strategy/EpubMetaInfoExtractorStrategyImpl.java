package com.andreidodu.europealibrary.batch.indexer.step.metainfo.dataextractor.strategy;

import com.andreidodu.europealibrary.batch.indexer.step.metainfo.dataextractor.MetaInfoExtractorStrategy;
import com.andreidodu.europealibrary.constants.DataPropertiesConst;
import com.andreidodu.europealibrary.dto.BookCodesDTO;
import com.andreidodu.europealibrary.model.*;
import com.andreidodu.europealibrary.repository.BookInfoRepository;
import com.andreidodu.europealibrary.repository.FileMetaInfoRepository;
import com.andreidodu.europealibrary.util.EpubUtil;
import com.andreidodu.europealibrary.util.StringUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Date;
import nl.siegmann.epublib.domain.Identifier;
import nl.siegmann.epublib.domain.Metadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Order(1)
@Component
@Transactional
@RequiredArgsConstructor
public class EpubMetaInfoExtractorStrategyImpl implements MetaInfoExtractorStrategy {
    final private static String STRATEGY_NAME = "epub-meta-info-extractor-strategy";

    private final EpubUtil epubUtil;
    private final DataExtractorStrategyUtil dataExtractorStrategyUtil;
    private final FileMetaInfoRepository fileMetaInfoRepository;
    private final BookInfoRepository bookInfoRepository;
    private final TagUtil tagUtil;
    @Value("${com.andreidodu.europea-library.job.indexer.step-indexer.disable-epub-metadata-extractor}")
    private boolean disableEpubMetadataExtractor;
    @Value("${com.andreidodu.europea-library.job.indexer.step-meta-info-writer.disable-isbn-extractor}")
    private boolean disableIsbExtractor;
    private final OtherMetaInfoExtractorStrategyImpl otherMetaInfoExtractorStrategy;
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public String getStrategyName() {
        return STRATEGY_NAME;
    }

    @Override
    public boolean accept(String filename, FileSystemItem fileSystemItem) {
        return dataExtractorStrategyUtil.isFileExtensionInWhiteList(epubUtil.getEpubFileExtension()) &&
                !disableEpubMetadataExtractor &&
                epubUtil.isEpub(filename) &&
                dataExtractorStrategyUtil.wasNotAlreadyProcessed(fileSystemItem);
    }


    @Override
    public Optional<FileMetaInfo> extract(String filename, FileSystemItem fileSystemItem) {
        log.debug("applying strategy: {}", getStrategyName());
        FileMetaInfo fileMetaInfo = fileSystemItem.getFileMetaInfo();
        try {
            return epubUtil.retrieveBook(filename)
                    .map(book -> {
                        if (book.getMetadata().getFirstTitle().trim().isEmpty()) {
                            log.debug("metadata not found for: {}", filename);
                            return this.otherMetaInfoExtractorStrategy.extract(filename, fileSystemItem).get();
                        }
                        log.debug("metadata found for: {}", filename);
                        return manageCaseBookTitleNotEmpty(book, filename, fileMetaInfo);
                    });
        } catch (Exception e) {
            log.error("invalid file: {} ({})", filename, e.getMessage());
            return this.otherMetaInfoExtractorStrategy.extract(filename, fileSystemItem);
        }
    }

    private FileMetaInfo manageCaseBookTitleNotEmpty(Book book, String fullPath, FileMetaInfo existingFileMetaInfo) {
        log.debug("gathering information from ebook {}", fullPath);
        FileMetaInfo fileMetaInfo = existingFileMetaInfo == null ? new FileMetaInfo() : existingFileMetaInfo;

        Metadata metadata = book.getMetadata();

        fileMetaInfo.setTitle(StringUtil.cleanAndTrimToNullSubstring(metadata.getFirstTitle(), DataPropertiesConst.FILE_META_INFO_TITLE_MAX_LENGTH));
        fileMetaInfo.setDescription(StringUtil.cleanAndTrimToNullSubstring(StringUtil.removeHTML(getFirst(metadata.getDescriptions())), DataPropertiesConst.FILE_META_INFO_DESCRIPTION_MAX_LENGTH));
        fileMetaInfo = this.fileMetaInfoRepository.save(fileMetaInfo);
        fileMetaInfo = buildBookInfo(book, fileMetaInfo, metadata);
        return fileMetaInfo;
    }

    private FileMetaInfo buildBookInfo(Book book, FileMetaInfo fileMetaInfo, Metadata metadata) {
        BookInfo bookInfo = fileMetaInfo.getBookInfo() != null ? fileMetaInfo.getBookInfo() : new BookInfo();
        bookInfo.setFileMetaInfo(fileMetaInfo);
        Optional.ofNullable(metadata.getLanguage())
                .ifPresent(language ->
                        bookInfo.setLanguage(StringUtil.cleanAndTrimToNullLowerCaseSubstring(language, DataPropertiesConst.BOOK_INFO_LANGUAGE_MAX_LENGTH)));
        bookInfo.setNumberOfPages(book.getContents().size());
        final List<String> authors = StringUtil.cleanAndTrimToNull(metadata.getAuthors().stream().map(auth -> auth.getFirstname() + " " + auth.getLastname()).collect(Collectors.toList()));
        if (!authors.isEmpty()) {
            bookInfo.setAuthors(StringUtil.cleanAndTrimToNullSubstring(String.join(",", authors), DataPropertiesConst.BOOK_INFO_AUTHORS_MAX_LENGTH));
        }

        extractISBN(metadata.getIdentifiers())
                .ifPresentOrElse(isbn -> {
                    if (isbn.length() == 13) {
                        bookInfo.setIsbn13(isbn);

                    } else if (isbn.length() == 10) {
                        bookInfo.setIsbn10(isbn);
                    }
                }, () -> {
                    if (!disableIsbExtractor) {
                        BookCodesDTO<Optional<String>, Optional<String>> bookCodes = this.epubUtil.extractISBN(book);
                        dataExtractorStrategyUtil.setISBN13(bookCodes, bookInfo);
                        dataExtractorStrategyUtil.setISBN10(bookCodes, bookInfo);
                    }
                });


        final List<String> publishers = StringUtil.cleanAndTrimToNull(metadata.getPublishers());
        if (!publishers.isEmpty()) {
            bookInfo.setPublisher(StringUtil.cleanAndTrimToNullSubstring(String.join(",", publishers), DataPropertiesConst.BOOK_INFO_PUBLISHER_MAX_LENGTH));
        }
        List<Date> dates = metadata.getDates();
        extractPublishedDate(dates)
                .ifPresent(date -> {
                    bookInfo.setPublishedDate(StringUtil.cleanAndTrimToNullSubstring(date, DataPropertiesConst.BOOK_INFO_PUBLISHED_DATE_MAX_LENGTH));
                });
        bookInfo.setFileMetaInfo(fileMetaInfo);
        bookInfo.setFileExtractionStatus(FileExtractionStatusEnum.SUCCESS.getStatus());
        fileMetaInfo.setBookInfo(this.bookInfoRepository.save(bookInfo));
        FileMetaInfo savedFileMetaInfo = this.fileMetaInfoRepository.save(fileMetaInfo);

        savedFileMetaInfo = dataExtractorStrategyUtil.createAndAssociateTags(metadata.getSubjects(), savedFileMetaInfo);

        return savedFileMetaInfo;
    }

    private Optional<String> extractISBN(List<Identifier> identifiers) {
        return identifiers.stream()
                .filter(id -> "ISBN".equals(id.getScheme()))
                .map(Identifier::getValue)
                .findFirst();


    }

    private Optional<String> extractPublishedDate(List<Date> dates) {
        return dates.stream().filter(date -> date.getEvent() == null).map(Date::getValue).findFirst();
    }

    private String getFirst(List<String> list) {
        if (list != null && !list.isEmpty()) {
            return list.stream().findFirst().get();
        }
        return null;
    }
}