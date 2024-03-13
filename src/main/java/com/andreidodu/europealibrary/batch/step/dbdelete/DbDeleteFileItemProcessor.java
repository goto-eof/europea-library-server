package com.andreidodu.europealibrary.batch.step.dbdelete;

import com.andreidodu.europealibrary.model.FileSystemItem;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@Transactional
@RequiredArgsConstructor
public class DbDeleteFileItemProcessor implements ItemProcessor<FileSystemItem, FileSystemItem> {


    @Override
    public FileSystemItem process(FileSystemItem item) throws Exception {
        return item;
    }
}
