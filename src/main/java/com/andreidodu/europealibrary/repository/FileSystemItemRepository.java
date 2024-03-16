package com.andreidodu.europealibrary.repository;

import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.common.TransactionalRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FileSystemItemRepository extends TransactionalRepository<FileSystemItem, Long>, CustomFileSystemItemRepository {
    Optional<FileSystemItem> findByBasePathAndNameAndJobStep(String basePAth, String name, int jobStep);

    @Query("select fsi from FileSystemItem fsi where id = (select min(id) from FileSystemItem) and jobStep=:jobStep")
    Optional<FileSystemItem> findByLowestId(Integer jobStep);
}
