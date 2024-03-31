package com.andreidodu.europealibrary.batch.indexer.step.externalapi;

import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.FileMetaInfoRepository;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalMetaInfoWriter implements ItemWriter<FileSystemItem> {
    private final FileMetaInfoRepository fileMetaInfoRepository;
    private final FileSystemItemRepository fileSystemItemRepository;

    @Override
    public void write(Chunk<? extends FileSystemItem> chunk) {
        // fileMetaInfoRepository.saveAll(chunk.getItems());
        chunk.getItems().forEach(this.fileSystemItemRepository::save);
    }
}
