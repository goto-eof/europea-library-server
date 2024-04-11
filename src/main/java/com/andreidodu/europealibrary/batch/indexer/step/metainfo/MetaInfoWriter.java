package com.andreidodu.europealibrary.batch.indexer.step.metainfo;

import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Deprecated
@RequiredArgsConstructor
public class MetaInfoWriter implements ItemWriter<FileSystemItem> {
    private final FileSystemItemRepository fileSystemItemRepository;
    @Value("${com.andreidodu.europea-library.job.indexer.step-meta-info-writer.batch-size}")
    private Integer batchSize;

    @Override
    public void write(Chunk<? extends FileSystemItem> chunk) {
        fileSystemItemRepository.saveAll(chunk.getItems());
        log.debug("saved {} records", chunk.getItems().size());
        fileSystemItemRepository.flush();
    }
}
