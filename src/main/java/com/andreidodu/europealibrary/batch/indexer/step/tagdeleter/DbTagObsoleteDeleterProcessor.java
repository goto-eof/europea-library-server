package com.andreidodu.europealibrary.batch.indexer.step.tagdeleter;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DbTagObsoleteDeleterProcessor implements ItemProcessor<Long, Long> {
    @Override
    public Long process(Long tagId) {
        return tagId;
    }
}
