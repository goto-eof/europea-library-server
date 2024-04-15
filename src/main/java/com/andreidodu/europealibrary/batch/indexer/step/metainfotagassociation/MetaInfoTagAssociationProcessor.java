package com.andreidodu.europealibrary.batch.indexer.step.metainfotagassociation;

import com.mysema.commons.lang.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MetaInfoTagAssociationProcessor implements ItemProcessor<Pair<Long, Long>, Pair<Long, Long>> {
    @Override
    public Pair<Long, Long> process(Pair<Long, Long> fileMetaInfoIdTagId) {
        return fileMetaInfoIdTagId;
    }

}
