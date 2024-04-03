package com.andreidodu.europealibrary.batch.indexer.step.dbfmiobsoletedeleter;

import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.repository.FileMetaInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DbFMIObsoleteDeleterWriter implements ItemWriter<FileMetaInfo> {
    final private FileMetaInfoRepository repository;

    @Override
    public void write(Chunk<? extends FileMetaInfo> chunk) {
        this.repository.deleteAllInBatch(chunk.getItems().stream().map(fmi -> (FileMetaInfo) fmi).collect(Collectors.toList()));
        log.info("deleted {} FileMetaInfo records", chunk.getItems().size());
    }
}
