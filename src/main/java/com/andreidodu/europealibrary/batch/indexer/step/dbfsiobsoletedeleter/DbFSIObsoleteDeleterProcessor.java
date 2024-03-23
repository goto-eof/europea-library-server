package com.andreidodu.europealibrary.batch.indexer.step.dbfsiobsoletedeleter;

import com.andreidodu.europealibrary.model.FileSystemItem;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DbFSIObsoleteDeleterProcessor implements ItemProcessor<FileSystemItem, FileSystemItem> {


    @Override
    public FileSystemItem process(FileSystemItem item) {
        return item;
    }
}
