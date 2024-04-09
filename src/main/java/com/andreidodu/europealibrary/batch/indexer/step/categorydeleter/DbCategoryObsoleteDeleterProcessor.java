package com.andreidodu.europealibrary.batch.indexer.step.categorydeleter;

import com.andreidodu.europealibrary.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DbCategoryObsoleteDeleterProcessor implements ItemProcessor<Long, Long> {
    private final CategoryRepository categoryRepository;

    @Override
    public Long process(Long categoryId) {
        return categoryId;
    }
}
