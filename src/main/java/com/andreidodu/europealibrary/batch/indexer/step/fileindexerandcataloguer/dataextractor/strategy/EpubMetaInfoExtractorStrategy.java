package com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer.dataextractor.strategy;

import com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer.dataextractor.MetaInfoExtractorStrategy;
import com.andreidodu.europealibrary.dto.BookCodesDTO;
import com.andreidodu.europealibrary.exception.ApplicationException;
import com.andreidodu.europealibrary.model.*;
import com.andreidodu.europealibrary.repository.FileMetaInfoRepository;
import com.andreidodu.europealibrary.repository.TagRepository;
import com.andreidodu.europealibrary.util.EpubUtil;
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
    private final MetaInfoExtractorStrategyCommon metaInfoExtractorStrategyCommon;
    private final FileMetaInfoRepository fileMetaInfoRepository;
    @Value("${com.andreidodu.europea-library.disable-epub-metadata-extractor}")
    private boolean disableEpubMetadataExtractor;

    @Override
    public String getStrategyName() {
        return STRATEGY_NAME;
    }

    @Override
    public boolean accept(String filename, FileSystemItem fileSystemItem) {
        return metaInfoExtractorStrategyCommon.isFileExtensionInWhiteList(epubUtil.getEpubFileExtension()) &&
                !disableEpubMetadataExtractor &&
                epubUtil.isEpub(filename) &&
                metaInfoExtractorStrategyCommon.wasNotAlreadyProcessed(fileSystemItem);
    }


    @Override
    public Optional<FileMetaInfo> extract(String filename, FileSystemItem fileSystemItem) {
        log.info("applying strategy: {}", getStrategyName());
        FileMetaInfo fileMetaInfo = fileSystemItem.getFileMetaInfo();
        try {
            return epubUtil.retrieveBook(filename)
                    .map(book -> {
                        if (book.getMetadata().getFirstTitle().trim().isEmpty()) {
                            return null;
                        }
                        return manageCaseBookTitleNotEmpty(book, filename, fileMetaInfo);
                    });
        } catch (Exception e) {
            throw new ApplicationException(e);
        }
    }

    private FileMetaInfo manageCaseBookTitleNotEmpty(Book book, String fullPath, FileMetaInfo existingFileMetaInfo) {
        log.info("gathering information from ebook {}", fullPath);
        FileMetaInfo fileMetaInfo = existingFileMetaInfo == null ? new FileMetaInfo() : existingFileMetaInfo;

        Metadata metadata = book.getMetadata();

        fileMetaInfo.setTitle(metadata.getFirstTitle());
        fileMetaInfo.setDescription(getFirst(metadata.getDescriptions()));
        this.fileMetaInfoRepository.save(fileMetaInfo);
        BookInfo bookInfo = buildBookInfo(book, fileMetaInfo, metadata);

        fileMetaInfo.setBookInfo(bookInfo);
        fileMetaInfo.getBookInfo().setIsInfoExtractedFromFile(true);
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
