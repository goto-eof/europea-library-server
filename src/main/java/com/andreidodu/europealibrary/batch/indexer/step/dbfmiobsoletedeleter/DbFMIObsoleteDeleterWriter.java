package com.andreidodu.europealibrary.batch.indexer.step.dbfmiobsoletedeleter;

import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.repository.FileMetaInfoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DbFMIObsoleteDeleterWriter implements ItemWriter<FileMetaInfo> {
    final private FileMetaInfoRepository repository;

    @Override
    public void write(Chunk<? extends FileMetaInfo> chunk) {
        this.repository.deleteAll(chunk.getItems());
        log.info("deleted {} records", chunk.getItems().size());
    }
}
