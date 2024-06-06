package com.andreidodu.europealibrary.repository;

import com.andreidodu.europealibrary.model.TemporaryResourceIdentifier;
import com.andreidodu.europealibrary.repository.common.TransactionalRepository;

import java.util.Optional;

public interface TemporaryResourceIdentifierRepository extends TransactionalRepository<TemporaryResourceIdentifier, Long> {
    Optional<TemporaryResourceIdentifier> findByFileSystemItem_id(Long fileSystemId);

    Optional<TemporaryResourceIdentifier> findByIdentifier(String resourceIdentifier);
}
