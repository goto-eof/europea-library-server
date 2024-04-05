package com.andreidodu.europealibrary.batch.indexer.step.tagdeleter;

import com.andreidodu.europealibrary.model.Tag;
import com.andreidodu.europealibrary.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DbTagObsoleteDeleterWriter implements ItemWriter<Tag> {
    final private TagRepository repository;

    @Override
    public void write(Chunk<? extends Tag> chunk) {
        this.repository.deleteAllInBatch(chunk.getItems().stream().map(tag -> (Tag) tag).collect(Collectors.toList()));
        log.info("deleted {} Tag records", chunk.getItems().size());
    }
}
