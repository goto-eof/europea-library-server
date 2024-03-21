package com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer.dataextractor;

import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.FileSystemItem;

import java.util.Optional;

public interface MetaInfoExtractorStrategy {

    String getStrategyName();

    boolean accept(String filename, FileSystemItem fileSystemItem);

    Optional<FileMetaInfo> extract(String filename, FileSystemItem fileSystemItem);
}
