package com.andreidodu.europealibrary.repository;

import com.andreidodu.europealibrary.model.Category;
import com.andreidodu.europealibrary.repository.common.TransactionalRepository;

import java.util.Optional;

public interface CategoryRepository extends TransactionalRepository<Category, Long> {
    Optional<Category> findByNameIgnoreCase(String category);
}
