package com.andreidodu.europealibrary.batch.indexer.step.externalapi.dataretriever;

import com.andreidodu.europealibrary.dto.ApiResponseDTO;
import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.FileSystemItem;

public interface MetaInfoRetrieverStrategy {
    String getStrategyName();

    boolean accept(FileSystemItem fileSystemItem);

    ApiResponseDTO<FileMetaInfo> process(FileSystemItem fileSystemItem);
}
