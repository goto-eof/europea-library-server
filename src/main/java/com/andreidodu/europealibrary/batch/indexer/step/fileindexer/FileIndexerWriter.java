package com.andreidodu.europealibrary.batch.indexer.step.fileindexer;

import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Slf4j
@Deprecated
@Component
@RequiredArgsConstructor
public class FileIndexerWriter implements ItemWriter<FileSystemItem> {
    private final FileSystemItemRepository fileSystemItemRepository;

    @Override
    public void write(Chunk<? extends FileSystemItem> chunk) {
        if (chunk.getItems().isEmpty()) {
            return;
        }
        log.debug("Saving {} elements", chunk.getItems().size());
        this.fileSystemItemRepository.saveAll(chunk.getItems());
        this.fileSystemItemRepository.flush();
    }

}
