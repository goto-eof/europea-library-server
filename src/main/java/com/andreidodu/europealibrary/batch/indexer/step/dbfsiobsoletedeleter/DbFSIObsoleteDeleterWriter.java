package com.andreidodu.europealibrary.batch.indexer.step.dbfsiobsoletedeleter;

import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.FeaturedFileMetaInfoRepository;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DbFSIObsoleteDeleterWriter implements ItemWriter<Long> {
    final private FileSystemItemRepository fileSystemItemRepository;
    final private FeaturedFileMetaInfoRepository featuredFileMetaInfoRepository;

    @Override
    public void write(Chunk<? extends Long> chunk) {
        this.fileSystemItemRepository.deleteAllByIdInBatch(new ArrayList<>(chunk.getItems()));
        this.fileSystemItemRepository.flush();
        log.debug("deleted {} records", chunk.getItems().size());
    }
}
