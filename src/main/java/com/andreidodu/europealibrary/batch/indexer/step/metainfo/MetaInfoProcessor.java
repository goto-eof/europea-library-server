package com.andreidodu.europealibrary.batch.indexer.step.metainfo;

import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MetaInfoProcessor implements ItemProcessor<Long, FileSystemItem> {
    final private MetaInfoRetriever metaInfoRetriever;

    final private FileSystemItemRepository fileSystemItemRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public FileSystemItem process(Long fileSystemItemId) {
        FileSystemItem fileSystemItem = this.fileSystemItemRepository.findById(fileSystemItemId).get();
        FileSystemItem fileSystemItemUpdated = metaInfoRetriever.buildMetaInfoFromEbookIfNecessary(fileSystemItem)
                .map(fileMetaInfo -> {
                    this.entityManager.detach(fileSystemItem);
                    fileSystemItem.setFileMetaInfoId(fileMetaInfo.getId());
                    return fileSystemItem;
                })
                .orElse(null);
        return fileSystemItemUpdated;
    }


}
