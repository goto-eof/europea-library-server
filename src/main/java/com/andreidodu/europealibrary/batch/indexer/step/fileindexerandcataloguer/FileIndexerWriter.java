package com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer;

import com.andreidodu.europealibrary.exception.ApplicationException;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileIndexerWriter implements ItemWriter<FileSystemItem> {
    private final FileSystemItemRepository fileSystemItemRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void write(Chunk<? extends FileSystemItem> chunk) {
        if (chunk.getItems().isEmpty()) {
            return;
        }
        Session session = entityManager.unwrap(Session.class);
        /*setting to one because children need to be associated to an existing parent*/
        session.setJdbcBatchSize(1);
        log.info("Saving {} elements", chunk.getItems().size());
        this.fileSystemItemRepository.saveAll(chunk.getItems());
        this.fileSystemItemRepository.flush();
    }

}
