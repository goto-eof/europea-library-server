package com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer.dataretriever;

import com.andreidodu.europealibrary.model.FileSystemItem;

public interface MetaInfoRetrieverStrategy {
    boolean accept(FileSystemItem fileSystemItem);

    void process(FileSystemItem fileSystemItem);
}
