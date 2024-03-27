package com.andreidodu.europealibrary.repository;

import com.andreidodu.europealibrary.model.Tag;
import com.andreidodu.europealibrary.repository.common.TransactionalRepository;

import java.util.Optional;

public interface TagRepository extends TransactionalRepository<Tag, Long>, CustomTagRepository {
    Optional<Tag> findByNameIgnoreCase(String tag);
}
