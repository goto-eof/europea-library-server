package com.andreidodu.europealibrary.repository;

import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.common.TransactionalRepository;
import feign.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FileSystemItemRepository extends TransactionalRepository<FileSystemItem, Long>, CustomFileSystemItemRepository {
    Optional<FileSystemItem> findByBasePathAndNameAndJobStep(String basePAth, String name, int jobStep);

    @Query("select fsi from FileSystemItem fsi where parentId is null and jobStep=:jobStep")
    Optional<FileSystemItem> findByNoParent(Integer jobStep);

    List<FileSystemItem> findBySha256(String hash);

    @Modifying
    @Query("update FileSystemItem fsi set fsi.fileMetaInfoId = :fileMetaInfoId where fsi.id = :fileSystemItemId")
    void updateFileMetaInfoId(@Param(value = "fileSystemItemId") Long fileSystemItemId, @Param(value = "fileMetaInfoId") Long fileMetaInfoId);

    List<FileSystemItem> findByBasePathAndNameAndJobStepIn(String basePath, String name, List<Integer> jobStepList);

    @Query(value = "select fsi.id from FileSystemItem fsi where fsi.basePath = :basePath and fsi.name = :name and fsi.jobStep = :jobStep")
    Optional<Long> findIdByBasePathAndNameAndJobStep(String basePath, String name, int jobStep);

}
