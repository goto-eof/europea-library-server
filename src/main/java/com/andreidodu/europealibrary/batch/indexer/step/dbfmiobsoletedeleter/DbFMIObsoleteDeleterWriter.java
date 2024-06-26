package com.andreidodu.europealibrary.batch.indexer.step.dbfmiobsoletedeleter;

import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.repository.BookInfoRepository;
import com.andreidodu.europealibrary.repository.FeaturedFileMetaInfoRepository;
import com.andreidodu.europealibrary.repository.FileMetaInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DbFMIObsoleteDeleterWriter implements ItemWriter<Long> {
    final private FileMetaInfoRepository fileMetaInfoRepository;
    final private BookInfoRepository bookInfoRepository;
    final private FeaturedFileMetaInfoRepository featuredFileMetaInfoRepository;

    @Override
    public void write(Chunk<? extends Long> chunk) {
        chunk.getItems().forEach(id -> {
            FileMetaInfo fileMetaInfo = this.fileMetaInfoRepository.findById(id).get();
            Optional.ofNullable(fileMetaInfo.getFeaturedFileMetaInfo())
                    .ifPresent(this.featuredFileMetaInfoRepository::delete);
        });
        this.bookInfoRepository.deleteAllByIdInBatch(this.bookInfoRepository.findAllByFileMetaInfoId((List<Long>) chunk.getItems()));
        this.bookInfoRepository.flush();
        this.fileMetaInfoRepository.deleteAllByIdInBatch((Iterable<Long>) chunk.getItems());
        this.fileMetaInfoRepository.flush();
        log.debug("deleted {} FileMetaInfo records", chunk.getItems().size());
    }
}
