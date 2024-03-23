package com.andreidodu.europealibrary.batch.indexer.step.dbfmiobsoletedeleter;

import com.andreidodu.europealibrary.model.FileMetaInfo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DbFMIObsoleteDeleterProcessor implements ItemProcessor<FileMetaInfo, FileMetaInfo> {


    @Override
    public FileMetaInfo process(FileMetaInfo item) {
        return item;
    }
}
