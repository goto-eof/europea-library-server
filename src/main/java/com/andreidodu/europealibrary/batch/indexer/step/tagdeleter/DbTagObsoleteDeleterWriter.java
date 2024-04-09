package com.andreidodu.europealibrary.batch.indexer.step.tagdeleter;

import com.andreidodu.europealibrary.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DbTagObsoleteDeleterWriter implements ItemWriter<Long> {
    final private TagRepository repository;

    @Override
    public void write(Chunk<? extends Long> chunk) {
        this.repository.deleteAllByIdInBatch((Iterable<Long>) chunk.getItems());
        this.repository.flush();
        log.debug("deleted {} Tag records", chunk.getItems().size());
    }
}
