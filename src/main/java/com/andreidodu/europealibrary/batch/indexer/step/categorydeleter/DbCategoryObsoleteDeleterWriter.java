package com.andreidodu.europealibrary.batch.indexer.step.categorydeleter;

import com.andreidodu.europealibrary.model.Category;
import com.andreidodu.europealibrary.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DbCategoryObsoleteDeleterWriter implements ItemWriter<Category> {
    final private CategoryRepository categoryRepository;

    @Override
    public void write(Chunk<? extends Category> chunk) {
        this.categoryRepository.deleteAllInBatch(chunk.getItems().stream().map(category -> (Category) category).collect(Collectors.toList()));
        log.debug("deleted {} Tag records", chunk.getItems().size());
    }
}
