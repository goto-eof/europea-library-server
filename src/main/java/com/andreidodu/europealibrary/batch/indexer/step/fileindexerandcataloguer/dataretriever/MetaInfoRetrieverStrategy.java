package com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer.dataretriever;

import com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer.dataretriever.strategy.ApiStatusEnum;
import com.andreidodu.europealibrary.model.FileSystemItem;

public interface MetaInfoRetrieverStrategy {
    String getStrategyName();

    boolean accept(FileSystemItem fileSystemItem);

    ApiStatusEnum process(FileSystemItem fileSystemItem);
}
