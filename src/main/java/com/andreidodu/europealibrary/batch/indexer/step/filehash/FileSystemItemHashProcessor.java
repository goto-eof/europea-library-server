package com.andreidodu.europealibrary.batch.indexer.step.filehash;

import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import com.andreidodu.europealibrary.util.FileUtil;
import com.andreidodu.europealibrary.util.StringUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileSystemItemHashProcessor implements ItemProcessor<Long, FileSystemItem> {
    private final FileUtil fileUtil;
    private final FileSystemItemRepository fileSystemItemRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public FileSystemItem process(Long fileSystemItemId) {
        FileSystemItem fileSystemItem = this.fileSystemItemRepository.findById(fileSystemItemId).get();
        this.entityManager.detach(fileSystemItem);
        if (isCalculateAndUpdateHashAndMetaInfoIdIfNecessary(fileSystemItem)) {
            return fileSystemItem;
        }
        return null;
    }

    private boolean isCalculateAndUpdateHashAndMetaInfoIdIfNecessary(FileSystemItem fileSystemItem) {
        if (fileSystemItem.getIsDirectory()) {
            return false;
        }


        if (StringUtil.isNotEmpty(fileSystemItem.getSha256())) {
            return true;
        }


        return isCalculateSha256(fileSystemItem);


    }

    private boolean isCalculateSha256(FileSystemItem fileSystemItem) {
        final String fileFullPath = fileSystemItem.getBasePath() + "/" + fileSystemItem.getName();
        return this.fileUtil.fileNameToHash(fileFullPath).map(sha256 -> {
                    fileSystemItem.setSha256(sha256);
                    return true;
                })
                .orElse(false);
    }

}
