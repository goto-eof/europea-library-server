package com.andreidodu.europealibrary.batch.indexer.step.externalapi.dataretriever.strategy;

import com.andreidodu.europealibrary.model.Category;
import com.andreidodu.europealibrary.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryUtil {
    private final CategoryRepository categoryRepository;

    private static Category createCategoryFromName(String categoryName) {
        Category category = new Category();
        category.setName(categoryName);
        return category;
    }

    @Retryable(retryFor = {DataIntegrityViolationException.class, NullPointerException.class}, maxAttemptsExpression = "1000000")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Category createCategoryEntity(String categoryName) {
        Optional<Category> tagOptional = this.categoryRepository.findByNameIgnoreCase(categoryName);
        Category category;
        if (tagOptional.isEmpty()) {
            category = createCategoryFromName(categoryName);
            category = this.categoryRepository.save(category);
            this.categoryRepository.flush();
        } else {
            category = tagOptional.get();
        }
        if (category == null || category.getId() == null) {
            throw new NullPointerException();
        }
        return category;
    }
}
