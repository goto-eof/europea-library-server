package com.andreidodu.europealibrary.batch.indexer.step.dbfsiobsoletedeleter;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DbFSIObsoleteDeleterProcessor implements ItemProcessor<Long, Long> {


    @Override
    public Long process(Long fileSystemItemId) {
        return fileSystemItemId;
    }
}
