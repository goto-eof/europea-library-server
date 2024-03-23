package com.andreidodu.europealibrary.batch.indexer.step.dbstepupdater;

import com.andreidodu.europealibrary.model.FileSystemItem;
import jakarta.transaction.Transactional;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class DbStepUpdaterProcessor implements ItemProcessor<FileSystemItem, FileSystemItem> {
    @Override
    public FileSystemItem process(FileSystemItem item) {
        return item;
    }
}
