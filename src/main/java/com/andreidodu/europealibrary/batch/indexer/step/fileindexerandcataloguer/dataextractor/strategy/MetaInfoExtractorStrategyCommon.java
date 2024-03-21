package com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer.dataextractor.strategy;

import com.andreidodu.europealibrary.model.FileSystemItem;

public class MetaInfoExtractorStrategyCommon {

    protected boolean wasAlreadyProcessed(FileSystemItem fileSystemItem) {
        if (fileSystemItem == null || fileSystemItem.getFileMetaInfo() == null || fileSystemItem.getFileMetaInfo().getBookInfo() == null) {
            return false;
        }
        return fileSystemItem.getFileMetaInfo().getBookInfo().getIsInfoExtractedFromFile();
    }
}
