package com.andreidodu.europealibrary.batch.step.dbdelete;

import com.andreidodu.europealibrary.batch.JobStepEnum;
import com.andreidodu.europealibrary.batch.step.file.FileItemWriter;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class DbDeleteFileItemWriter implements ItemWriter<FileSystemItem> {
    final private FileSystemItemRepository fileSystemItemRepository;

    @Override
    public void write(Chunk<? extends FileSystemItem> chunk) {
        chunk.getItems().forEach(item -> {
            this.fileSystemItemRepository.findByBasePathAndNameAndJobStep(item.getBasePath(), item.getName(), JobStepEnum.INSERTED.getStepNumber()).ifPresent(newItem -> {
                newItem.setFileMetaInfo(item.getFileMetaInfo());
            });
        });
        this.fileSystemItemRepository.deleteAll(chunk.getItems());
        log.info("deleted {} records", chunk.getItems().size());
    }
}
