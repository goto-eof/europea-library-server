package com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer.dataretriever;

import com.andreidodu.europealibrary.dto.ApiResponseDTO;
import com.andreidodu.europealibrary.dto.FileMetaInfoBookDTO;
import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.FileSystemItem;

public interface MetaInfoRetrieverStrategy {
    String getStrategyName();

    boolean accept(FileSystemItem fileSystemItem);

    ApiResponseDTO<FileMetaInfo> process(FileSystemItem fileSystemItem);
}
