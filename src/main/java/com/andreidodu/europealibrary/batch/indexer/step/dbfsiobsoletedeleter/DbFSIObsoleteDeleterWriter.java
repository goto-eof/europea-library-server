package com.andreidodu.europealibrary.batch.indexer.step.dbfsiobsoletedeleter;

import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DbFSIObsoleteDeleterWriter implements ItemWriter<FileSystemItem> {
    final private FileSystemItemRepository fileSystemItemRepository;

    @Override
    public void write(Chunk<? extends FileSystemItem> chunk) {
        this.fileSystemItemRepository.deleteAll(chunk.getItems());
        log.info("deleted {} records", chunk.getItems().size());
    }
}
