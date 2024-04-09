package com.andreidodu.europealibrary.batch.indexer.step.categorydeleter;

import com.andreidodu.europealibrary.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DbCategoryObsoleteDeleterWriter implements ItemWriter<Long> {
    final private CategoryRepository categoryRepository;

    @Override
    public void write(Chunk<? extends Long> chunk) {
        this.categoryRepository.deleteAllByIdInBatch((Iterable<Long>) chunk.getItems());
        this.categoryRepository.flush();
        log.debug("deleted {} Tag records", chunk.getItems().size());
    }
}
