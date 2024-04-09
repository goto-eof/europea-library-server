package com.andreidodu.europealibrary.batch.indexer.step.dbfmiobsoletedeleter;

import com.andreidodu.europealibrary.repository.FileMetaInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DbFMIObsoleteDeleterProcessor implements ItemProcessor<Long, Long> {
    private final FileMetaInfoRepository fileMetaInfoRepository;

    @Override
    public Long process(Long fileMetaInfoId) {
        return fileMetaInfoId;
    }
}
