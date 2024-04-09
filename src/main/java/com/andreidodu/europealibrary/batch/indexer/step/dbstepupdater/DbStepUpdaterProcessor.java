package com.andreidodu.europealibrary.batch.indexer.step.dbstepupdater;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DbStepUpdaterProcessor implements ItemProcessor<Long, Long> {
    @Override
    public Long process(Long fileSystemItemId) {
        return fileSystemItemId;
    }
}
