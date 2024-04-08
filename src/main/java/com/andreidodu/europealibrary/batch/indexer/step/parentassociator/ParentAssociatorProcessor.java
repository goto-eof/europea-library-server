package com.andreidodu.europealibrary.batch.indexer.step.parentassociator;

import com.andreidodu.europealibrary.batch.indexer.enums.JobStepEnum;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import com.andreidodu.europealibrary.util.FileUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ParentAssociatorProcessor implements ItemProcessor<Long, FileSystemItem> {
    final private FileSystemItemRepository fileSystemItemRepository;
    final private FileUtil fileUtil;
    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public FileSystemItem process(Long fileSystemItemId) {
        FileSystemItem fileSystemItem = this.fileSystemItemRepository.findById(fileSystemItemId).get();
        recalculateParent(fileSystemItem);
        return fileSystemItem;
    }

    private void recalculateParent(FileSystemItem fileSystemItem) {
        this.getFileSystemItemByPathNameAndJobStep(fileUtil.calculateParentBasePath(fileSystemItem.getBasePath()), fileUtil.calculateFileName(fileSystemItem.getBasePath()), JobStepEnum.INSERTED.getStepNumber())
                .ifPresentOrElse(fileSystemItem::setParentId, () -> fileSystemItem.setParent(null));
    }

    private Optional<Long> getFileSystemItemByPathNameAndJobStep(String basePath, String name, int jobStep) {
        return this.fileSystemItemRepository.findIdByBasePathAndNameAndJobStep(basePath, name, jobStep);
    }
}
