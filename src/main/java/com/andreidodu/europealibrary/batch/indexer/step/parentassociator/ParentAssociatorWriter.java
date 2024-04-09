package com.andreidodu.europealibrary.batch.indexer.step.parentassociator;

import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Slf4j
@Component
@Deprecated
@RequiredArgsConstructor
public class ParentAssociatorWriter implements ItemWriter<FileSystemItem> {
    final private FileSystemItemRepository fileSystemItemRepository;

    @Override
    public void write(Chunk<? extends FileSystemItem> chunk) {
        this.fileSystemItemRepository.saveAll(chunk.getItems().stream().map(fsi -> (FileSystemItem) fsi).collect(Collectors.toList()));
        this.fileSystemItemRepository.flush();
        log.debug("saved {} records", chunk.getItems().size());
    }
}
