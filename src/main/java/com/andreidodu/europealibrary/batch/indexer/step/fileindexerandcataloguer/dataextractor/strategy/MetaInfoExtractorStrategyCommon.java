package com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer.dataextractor.strategy;

import com.andreidodu.europealibrary.model.FileSystemItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MetaInfoExtractorStrategyCommon {
    @Value("${com.andreidodu.europea-library.do-not-extract-metadata-from-file-extensions}")
    List<String> doNotProcessFileExtensions;

    public boolean wasNotAlreadyProcessed(FileSystemItem fileSystemItem) {
        if (fileSystemItem == null || fileSystemItem.getFileMetaInfo() == null || fileSystemItem.getFileMetaInfo().getBookInfo() == null) {
            return true;
        }
        return fileSystemItem.getFileMetaInfo().getBookInfo().getIsInfoExtractedFromFile() != null &&
                fileSystemItem.getFileMetaInfo().getBookInfo().getIsInfoExtractedFromFile();
    }

    public boolean isFileExtensionInWhiteList(String fileExtension) {
        return doNotProcessFileExtensions.stream()
                .noneMatch(ext -> ext.equalsIgnoreCase(fileExtension));
    }

}
