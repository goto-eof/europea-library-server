package com.andreidodu.europealibrary.batch.indexer.step.dbstepupdater;

import com.andreidodu.europealibrary.batch.indexer.enums.JobStepEnum;
import com.andreidodu.europealibrary.batch.indexer.enums.RecordStatusEnum;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DbStepUpdaterProcessor implements ItemProcessor<Long, FileSystemItem> {
    private final FileSystemItemRepository fileSystemItemRepository;

    @Override
    public FileSystemItem process(Long fileSystemItemId) {
        FileSystemItem fileSystemItem = this.fileSystemItemRepository.findById(fileSystemItemId).get();
        fileSystemItem.setJobStep(JobStepEnum.READY.getStepNumber());
        fileSystemItem.setRecordStatus(RecordStatusEnum.ENABLED.getStatus());
        return fileSystemItem;
    }
}
