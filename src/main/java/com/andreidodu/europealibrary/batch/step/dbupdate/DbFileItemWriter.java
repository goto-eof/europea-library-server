package com.andreidodu.europealibrary.batch.step.dbupdate;

import com.andreidodu.europealibrary.batch.JobStepEnum;
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
public class DbFileItemWriter implements ItemWriter<FileSystemItem> {
    private final FileSystemItemRepository fileSystemItemRepository;

    @Override
    public void write(Chunk<? extends FileSystemItem> chunk) throws Exception {
        chunk.getItems().forEach(item -> {
            item.setJobStep(JobStepEnum.READY.getStepNumber());
            fileSystemItemRepository.save(item);
            log.info("changed to ready: " + item);
        });
        fileSystemItemRepository.flush();
    }
}
