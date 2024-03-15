package com.andreidodu.europealibrary.batch.step.dbdelete;

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
//        chunk.getItems().forEach(model -> {
//            //if (this.fileSystemItemRepository.existsById(model.getId())) {
//            this.fileSystemItemRepository.delete(model);
//            //}
//        });
        this.fileSystemItemRepository.deleteAll(chunk.getItems());
        log.info("records deleted: " + chunk);
        fileSystemItemRepository.flush();
    }
}
