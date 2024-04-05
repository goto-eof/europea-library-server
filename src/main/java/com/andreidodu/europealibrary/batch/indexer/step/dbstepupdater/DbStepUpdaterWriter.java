package com.andreidodu.europealibrary.batch.indexer.step.dbstepupdater;

import com.andreidodu.europealibrary.batch.indexer.enums.JobStepEnum;
import com.andreidodu.europealibrary.batch.indexer.enums.RecordStatusEnum;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DbStepUpdaterWriter implements ItemWriter<FileSystemItem> {
    private final FileSystemItemRepository fileSystemItemRepository;

    @Override
    public void write(Chunk<? extends FileSystemItem> chunk) {
        fileSystemItemRepository.saveAll(chunk.getItems());
        this.fileSystemItemRepository.flush();
        log.info("changed to ready status {} fileSystemItem entities", chunk.getItems().size());
    }
}
