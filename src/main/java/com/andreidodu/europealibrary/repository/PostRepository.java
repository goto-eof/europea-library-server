package com.andreidodu.europealibrary.repository;

import com.andreidodu.europealibrary.model.Post;
import com.andreidodu.europealibrary.repository.common.TransactionalRepository;

import java.util.Optional;

public interface PostRepository extends TransactionalRepository<Post, Long> {
    Optional<Post> findByIdentifier(String identifier);
}
