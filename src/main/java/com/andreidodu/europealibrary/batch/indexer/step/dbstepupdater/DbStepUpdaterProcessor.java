package com.andreidodu.europealibrary.batch.indexer.step.dbstepupdater;

import com.andreidodu.europealibrary.batch.indexer.enums.JobStepEnum;
import com.andreidodu.europealibrary.batch.indexer.enums.RecordStatusEnum;
import com.andreidodu.europealibrary.model.FileSystemItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class DbStepUpdaterProcessor implements ItemProcessor<FileSystemItem, FileSystemItem> {
    @Override
    public FileSystemItem process(FileSystemItem item) {
        item.setJobStep(JobStepEnum.READY.getStepNumber());
        item.setRecordStatus(RecordStatusEnum.ENABLED.getStatus());
        log.info("changed to ready: " + item);
        return item;
    }
}
