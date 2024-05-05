package com.andreidodu.europealibrary.repository;

import com.andreidodu.europealibrary.model.FeaturedFileSystemItem;
import com.andreidodu.europealibrary.model.Post;
import com.andreidodu.europealibrary.repository.common.TransactionalRepository;

public interface PostRepository extends TransactionalRepository<Post, Long> {
}
