package com.andreidodu.europealibrary.repository;

import com.andreidodu.europealibrary.model.FileSystemItemTopDownloadsView;
import com.andreidodu.europealibrary.repository.common.TransactionalRepository;

public interface FileSystemItemTopDownloadsViewRepository extends TransactionalRepository<FileSystemItemTopDownloadsView, Long>, CustomCategoryRepository {
}
