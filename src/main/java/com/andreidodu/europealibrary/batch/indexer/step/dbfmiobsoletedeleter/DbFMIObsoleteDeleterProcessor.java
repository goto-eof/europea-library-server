package com.andreidodu.europealibrary.batch.indexer.step.dbfmiobsoletedeleter;

import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.FileMetaInfoRepository;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DbFMIObsoleteDeleterProcessor implements ItemProcessor<Long, FileMetaInfo> {
    private final FileMetaInfoRepository fileMetaInfoRepository;

    @Override
    public FileMetaInfo process(Long fileMetaInfoId) {
        return this.fileMetaInfoRepository.findById(fileMetaInfoId).get();
    }
}
