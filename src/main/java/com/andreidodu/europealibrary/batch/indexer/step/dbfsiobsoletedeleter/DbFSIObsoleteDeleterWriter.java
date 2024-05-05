package com.andreidodu.europealibrary.batch.indexer.step.dbfsiobsoletedeleter;

import com.andreidodu.europealibrary.model.FeaturedFileSystemItem;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.CustomFeaturedFileSystemItemRepository;
import com.andreidodu.europealibrary.repository.FeaturedFileSystemRepository;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import com.andreidodu.europealibrary.resource.FeaturedFileSystemItemResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DbFSIObsoleteDeleterWriter implements ItemWriter<Long> {
    final private FileSystemItemRepository fileSystemItemRepository;
    final private FeaturedFileSystemRepository featuredFileSystemRepository;

    @Override
    public void write(Chunk<? extends Long> chunk) {
        chunk.getItems().forEach(id -> {
            FileSystemItem fileSystemItem = this.fileSystemItemRepository.findById(id).get();
            Optional.ofNullable(fileSystemItem.getFeaturedFileSystemItem())
                    .ifPresent(this.featuredFileSystemRepository::delete);
            this.fileSystemItemRepository.delete(fileSystemItem);
        });
        this.fileSystemItemRepository.flush();
        log.debug("deleted {} records", chunk.getItems().size());
    }
}
