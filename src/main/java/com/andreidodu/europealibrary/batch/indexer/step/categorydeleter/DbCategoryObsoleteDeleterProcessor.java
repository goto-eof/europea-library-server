package com.andreidodu.europealibrary.batch.indexer.step.categorydeleter;

import com.andreidodu.europealibrary.model.Category;
import com.andreidodu.europealibrary.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DbCategoryObsoleteDeleterProcessor implements ItemProcessor<Long, Category> {
    private final CategoryRepository categoryRepository;

    @Override
    public Category process(Long categoryId) {
        return this.categoryRepository.findById(categoryId).get();
    }
}
