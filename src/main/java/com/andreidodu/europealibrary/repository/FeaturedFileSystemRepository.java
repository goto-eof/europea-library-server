package com.andreidodu.europealibrary.repository;

import com.andreidodu.europealibrary.model.FeaturedFileSystemItem;
import com.andreidodu.europealibrary.repository.common.TransactionalRepository;

import java.util.Optional;

public interface FeaturedFileSystemRepository extends TransactionalRepository<FeaturedFileSystemItem, Long>, CustomFeaturedFileSystemItemRepository {
    Optional<FeaturedFileSystemItem> findByFileSystemItem_id(Long fileSystemItemId);
}
