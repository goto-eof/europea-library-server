package com.andreidodu.europealibrary.batch.indexer.step.dbstepupdater;

import com.andreidodu.europealibrary.batch.indexer.JobStepEnum;
import com.andreidodu.europealibrary.batch.indexer.RecordStatusEnum;
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
public class DbStepUpdaterWriter implements ItemWriter<FileSystemItem> {
    private final FileSystemItemRepository fileSystemItemRepository;

    @Override
    public void write(Chunk<? extends FileSystemItem> chunk) {
        chunk.getItems().forEach(item -> {
            item.setJobStep(JobStepEnum.READY.getStepNumber());
            item.setRecordStatus(RecordStatusEnum.ENABLED.getStatus());
            fileSystemItemRepository.save(item);
            log.info("changed to ready: " + item);
        });
    }
}
