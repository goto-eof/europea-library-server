package com.andreidodu.europealibrary.batch.indexer.step.dbfsiobsoletedeleter;

import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.FileSystemItem;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Transactional
@RequiredArgsConstructor
public class DbFSIObsoleteDeleterProcessor implements ItemProcessor<FileSystemItem, FileSystemItem> {


    @Override
    public FileSystemItem process(FileSystemItem item) {
        return item;
    }
}
