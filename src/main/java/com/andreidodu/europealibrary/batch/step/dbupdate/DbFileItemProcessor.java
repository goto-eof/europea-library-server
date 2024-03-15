package com.andreidodu.europealibrary.batch.step.dbupdate;

import com.andreidodu.europealibrary.model.FileSystemItem;
import jakarta.transaction.Transactional;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@Transactional
public class DbFileItemProcessor implements ItemProcessor<FileSystemItem, FileSystemItem> {
    @Override
    public FileSystemItem process(FileSystemItem item) {
        return item;
    }
}
