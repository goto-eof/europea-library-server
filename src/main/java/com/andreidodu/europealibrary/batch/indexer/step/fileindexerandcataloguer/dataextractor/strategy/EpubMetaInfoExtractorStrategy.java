package com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer.dataextractor.strategy;

import com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer.dataextractor.MetaInfoExtractorStrategy;
import com.andreidodu.europealibrary.dto.BookCodesDTO;
import com.andreidodu.europealibrary.model.*;
import com.andreidodu.europealibrary.repository.BookInfoRepository;
import com.andreidodu.europealibrary.repository.FileMetaInfoRepository;
import com.andreidodu.europealibrary.repository.TagRepository;
import com.andreidodu.europealibrary.util.EpubUtil;
import com.andreidodu.europealibrary.util.StringUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class EpubMetaInfoExtractorStrategy implements MetaInfoExtractorStrategy {
    final private static String STRATEGY_NAME = "epub-meta-info-extractor-strategy";

    private final EpubUtil epubUtil;
    private final DataExtractorStrategyUtil dataExtractorStrategyUtil;
    private final TagRepository tagRepository;
    private final FileMetaInfoRepository fileMetaInfoRepository;
    private final BookInfoRepository bookInfoRepository;
    @Value("${com.andreidodu.europea-library.job.indexer.step-indexer.disable-epub-metadata-extractor}")
    private boolean disableEpubMetadataExtractor;

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
                            return manageCaseBookMetadataNotAvailable(filename, fileSystemItem, FileExtractionStatusEnum.SUCCESS_EMPTY);
                        }
                        log.info("metadata found for: {}", filename);
                        return manageCaseBookTitleNotEmpty(book, filename, fileMetaInfo);
                    });
        } catch (Exception e) {
            log.info("invalid file: {}", filename);
            return Optional.of(manageCaseBookMetadataNotAvailable(filename, fileSystemItem, FileExtractionStatusEnum.FAILED));
        }
    }

    private FileMetaInfo manageCaseBookMetadataNotAvailable(String filename, FileSystemItem fileSystemItem, FileExtractionStatusEnum fileExtractionStatusEnum) {
        FileMetaInfo existingFileMetaInfo = fileSystemItem.getFileMetaInfo();
        FileMetaInfo fileMetaInfo = existingFileMetaInfo == null ? new FileMetaInfo() : existingFileMetaInfo;
        fileMetaInfo.setTitle(filename);
        fileMetaInfo = this.fileMetaInfoRepository.save(fileMetaInfo);
        BookInfo bookInfo = buildBookInfo(fileMetaInfo);
        fileMetaInfo.setBookInfo(bookInfo);
        fileMetaInfo.getBookInfo().setFileExtractionStatus(fileExtractionStatusEnum.getStatus());
        bookInfoRepository.save(bookInfo);
        return fileMetaInfo;
    }

    private BookInfo buildBookInfo(FileMetaInfo fileMetaInfo) {
        BookInfo oldBookInfo = fileMetaInfo.getBookInfo();
        BookInfo bookInfo = oldBookInfo != null ? oldBookInfo : new BookInfo();
        bookInfo.setFileMetaInfo(fileMetaInfo);
        return bookInfo;
    }

    private FileMetaInfo manageCaseBookTitleNotEmpty(Book book, String fullPath, FileMetaInfo existingFileMetaInfo) {
        log.info("gathering information from ebook {}", fullPath);
        FileMetaInfo fileMetaInfo = existingFileMetaInfo == null ? new FileMetaInfo() : existingFileMetaInfo;

        Metadata metadata = book.getMetadata();

        fileMetaInfo.setTitle(StringUtil.clean(metadata.getFirstTitle()));
        fileMetaInfo.setDescription(StringUtil.clean(getFirst(metadata.getDescriptions())));
        fileMetaInfo = this.fileMetaInfoRepository.save(fileMetaInfo);
        BookInfo bookInfo = buildBookInfo(book, fileMetaInfo, metadata);
        bookInfo.setFileMetaInfo(fileMetaInfo);
        bookInfo.setFileExtractionStatus(FileExtractionStatusEnum.SUCCESS.getStatus());
        this.bookInfoRepository.save(bookInfo);
        return fileMetaInfo;
    }

    private BookInfo buildBookInfo(Book book, FileMetaInfo fileMetaInfo, Metadata metadata) {
        BookInfo bookInfo = fileMetaInfo.getBookInfo() != null ? fileMetaInfo.getBookInfo() : new BookInfo();
        bookInfo.setFileMetaInfo(fileMetaInfo);
        Optional.ofNullable(metadata.getLanguage())
                .ifPresent(language ->
                        bookInfo.setLanguage(language.substring(0, Math.min(language.length(), 10))));
        bookInfo.setNumberOfPages(book.getContents().size());
        bookInfo.setAuthors(String.join(", ", metadata.getAuthors()
                .stream()
                .map(author -> author.getFirstname() + " " + author.getLastname())
                .toList()));
        BookCodesDTO<Optional<String>, Optional<String>> bookCodes = this.epubUtil.extractISBN(book);
        dataExtractorStrategyUtil.setISBN13(bookCodes, bookInfo);
        dataExtractorStrategyUtil.setISBN10(bookCodes, bookInfo);
        bookInfo.setPublisher(String.join(", ", metadata.getPublishers()));
        addTagsIfPresent(metadata, fileMetaInfo);
        return bookInfo;
    }

    private void addTagsIfPresent(Metadata metadata, FileMetaInfo fileMetaInfo) {
        log.info("EPUB METADATA: {}", metadata.toString());
        Optional.ofNullable(metadata.getSubjects())
                .ifPresent(tags -> {
                    if (fileMetaInfo.getTagList() == null) {
                        fileMetaInfo.setTagList(new ArrayList<>());
                    }
                    tags.stream()
                            .filter(tag -> !tag.trim().isEmpty())
                            .forEach(tag -> {
                                String resizedTag = tag.substring(0, Math.min(tag.length(), 100));
                                Optional<Tag> tagOptional = this.tagRepository.findByNameIgnoreCase(resizedTag);
                                tagOptional.ifPresentOrElse(tagEntity -> {
                                            if (!fileMetaInfo.getTagList().contains(tagEntity)) {
                                                fileMetaInfo.getTagList().add(tagEntity);
                                            }
                                        },
                                        () -> {
                                            Tag tagEntity = new Tag();
                                            tagEntity.setName(resizedTag);
                                            this.tagRepository.save(tagEntity);
                                            fileMetaInfo.getTagList().add(tagEntity);
                                        }
                                );

                            });
                });
    }

    private String getFirst(List<String> list) {
        if (list != null && !list.isEmpty()) {
            list.getFirst();
        }
        return null;
    }
}
