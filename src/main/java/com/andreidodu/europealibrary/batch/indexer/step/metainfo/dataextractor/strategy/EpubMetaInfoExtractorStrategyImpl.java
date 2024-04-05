package com.andreidodu.europealibrary.batch.indexer.step.metainfo.dataextractor.strategy;

import com.andreidodu.europealibrary.batch.indexer.step.common.StepUtil;
import com.andreidodu.europealibrary.batch.indexer.step.metainfo.dataextractor.MetaInfoExtractorStrategy;
import com.andreidodu.europealibrary.constants.DataPropertiesConst;
import com.andreidodu.europealibrary.dto.BookCodesDTO;
import com.andreidodu.europealibrary.exception.ApplicationException;
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
    private final OtherMetaInfoExtractorStrategyImpl otherMetaInfoExtractorStrategy;
    @PersistenceContext
    private EntityManager entityManager;
    private final StepUtil stepUtil;

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
        log.info("applying strategy: {}", getStrategyName());
        FileMetaInfo fileMetaInfo = fileSystemItem.getFileMetaInfo();
        try {
            return epubUtil.retrieveBook(filename)
                    .map(book -> {
                        if (book.getMetadata().getFirstTitle().trim().isEmpty()) {
                            log.info("metadata not found for: {}", filename);
                            return this.otherMetaInfoExtractorStrategy.extract(filename, fileSystemItem).get();
                        }
                        log.info("metadata found for: {}", filename);
                        return manageCaseBookTitleNotEmpty(book, filename, fileMetaInfo);
                    });
        } catch (Exception e) {
            log.info("invalid file: {} ({})", filename, e.getMessage());
            return this.otherMetaInfoExtractorStrategy.extract(filename, fileSystemItem);
        }
    }

    private FileMetaInfo manageCaseBookTitleNotEmpty(Book book, String fullPath, FileMetaInfo existingFileMetaInfo) {
        log.info("gathering information from ebook {}", fullPath);
        FileMetaInfo fileMetaInfo = existingFileMetaInfo == null ? new FileMetaInfo() : existingFileMetaInfo;

        Metadata metadata = book.getMetadata();

        fileMetaInfo.setTitle(StringUtil.clean(metadata.getFirstTitle()));
        fileMetaInfo.setDescription(StringUtil.substring(StringUtil.removeHTML(getFirst(metadata.getDescriptions())), DataPropertiesConst.FILE_META_INFO_DESCRIPTION_MAX_LENGTH));
        fileMetaInfo = this.fileMetaInfoRepository.save(fileMetaInfo);
        fileMetaInfo = buildBookInfo(book, fileMetaInfo, metadata);
        return fileMetaInfo;
    }

    private FileMetaInfo buildBookInfo(Book book, FileMetaInfo fileMetaInfo, Metadata metadata) {
        BookInfo bookInfo = fileMetaInfo.getBookInfo() != null ? fileMetaInfo.getBookInfo() : new BookInfo();
        bookInfo.setFileMetaInfo(fileMetaInfo);
        Optional.ofNullable(metadata.getLanguage())
                .ifPresent(language ->
                        bookInfo.setLanguage(StringUtil.clean(language.substring(0, Math.min(language.length(), 10)))));
        bookInfo.setNumberOfPages(book.getContents().size());
        final List<String> authors = StringUtil.cleanAndTrimToNull(metadata.getAuthors().stream().map(auth -> auth.getFirstname() + " " + auth.getLastname()).collect(Collectors.toList()));
        if (!authors.isEmpty()) {
            bookInfo.setAuthors(String.join(",", authors));
        }
        BookCodesDTO<Optional<String>, Optional<String>> bookCodes = this.epubUtil.extractISBN(book);
        dataExtractorStrategyUtil.setISBN13(bookCodes, bookInfo);
        dataExtractorStrategyUtil.setISBN10(bookCodes, bookInfo);
        final List<String> publishers = StringUtil.cleanAndTrimToNull(metadata.getPublishers());
        if (!publishers.isEmpty()) {
            bookInfo.setPublisher(String.join(",", publishers));
        }

        bookInfo.setFileMetaInfo(fileMetaInfo);
        bookInfo.setFileExtractionStatus(FileExtractionStatusEnum.SUCCESS.getStatus());
        fileMetaInfo.setBookInfo(this.bookInfoRepository.save(bookInfo));
        FileMetaInfo savedFileMetaInfo = this.fileMetaInfoRepository.save(fileMetaInfo);

        savedFileMetaInfo = createAndAssociateTags(metadata, savedFileMetaInfo);

        return savedFileMetaInfo;
    }

    private FileMetaInfo createAndAssociateTags(Metadata metadata, FileMetaInfo savedFileMetaInfo) {
        try {
            List<String> items = new ArrayList<>(this.stepUtil.explodeInUniqueItems(metadata.getSubjects()));
            Set<Tag> explodedTags = this.stepUtil.createOrLoadItems(items);
            savedFileMetaInfo = this.stepUtil.associateTags(savedFileMetaInfo, explodedTags);
        } catch (Exception e) {
            log.error("something went wrong with tag creation/association: {}", e.getMessage());
        }
        return savedFileMetaInfo;
    }


    private String getFirst(List<String> list) {
        if (list != null && !list.isEmpty()) {
            return list.stream().findFirst().get();
        }
        return null;
    }
}