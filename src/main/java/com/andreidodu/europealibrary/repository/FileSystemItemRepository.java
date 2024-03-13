package com.andreidodu.europealibrary.repository;

import com.andreidodu.europealibrary.model.FileSystemItem;

import java.util.Optional;

public interface FileSystemItemRepository extends TransactionalRepository<FileSystemItem, Long> {
    Optional<FileSystemItem> findByBasePathAndNameAndJobStep(String basePAth, String name, int jobStep);
}
