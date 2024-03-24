package com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer.dataextractor.strategy;

import com.andreidodu.europealibrary.dto.BookCodesDTO;
import com.andreidodu.europealibrary.model.BookInfo;
import com.andreidodu.europealibrary.model.FileSystemItem;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@Transactional
public class DataExtractorStrategyUtil {
    @Value("${com.andreidodu.europea-library.job.indexer.step-indexer.do-not-extract-metadata-from-file-extensions}")
    List<String> doNotProcessFileExtensions;

    public boolean wasNotAlreadyProcessed(FileSystemItem fileSystemItem) {
        if (fileSystemItem == null || fileSystemItem.getFileMetaInfo() == null || fileSystemItem.getFileMetaInfo().getBookInfo() == null) {
            return true;
        }
        Integer fileExtractionStatus = fileSystemItem.getFileMetaInfo().getBookInfo().getFileExtractionStatus();
        List<Integer> statusesToIgnore = List.of(FileExtractionStatusEnum.SUCCESS.getStatus(), FileExtractionStatusEnum.SUCCESS_EMPTY.getStatus(), FileExtractionStatusEnum.FAILED.getStatus());
        return fileExtractionStatus == null || !statusesToIgnore.contains(fileExtractionStatus);

    }

    public boolean isFileExtensionInWhiteList(String fileExtension) {
        return doNotProcessFileExtensions.stream()
                .noneMatch(ext -> ext.equalsIgnoreCase(fileExtension));
    }

    public void setISBN13(BookCodesDTO<Optional<String>, Optional<String>> bookCodes, BookInfo bookInfo) {
        bookCodes.getIsbn13()
                .ifPresent(isbn -> {
                    bookInfo.setIsbn13(isbn);
                    log.info("ISBN-13 found: {}", isbn);
                });
    }

    public void setISBN10(BookCodesDTO<Optional<String>, Optional<String>> bookCodes, BookInfo bookInfo) {
        bookCodes.getIsbn10()
                .ifPresent(sbn -> {
                    bookInfo.setIsbn10(sbn);
                    log.info("ISBN-10 found: {}", sbn);
                });
    }

}
