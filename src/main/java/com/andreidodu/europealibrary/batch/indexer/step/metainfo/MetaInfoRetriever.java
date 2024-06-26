package com.andreidodu.europealibrary.batch.indexer.step.metainfo;

import com.andreidodu.europealibrary.batch.indexer.step.metainfo.dataextractor.MetaInfoExtractorStrategy;
import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.FileMetaInfoRepository;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MetaInfoRetriever {
    final private List<MetaInfoExtractorStrategy> metaInfoExtractorStrategyList;
    final private FileMetaInfoRepository fileMetaInfoRepository;
    final private FileSystemItemRepository fileSystemItemRepository;
    @Value("${com.andreidodu.europea-library.job.indexer.avoid-duplicate-meta-info}")
    private boolean avoidDuplicateMetaInfo;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Optional<FileMetaInfo> buildMetaInfoFromEbookIfNecessary(FileSystemItem fileSystemItem) {
        String fullPath = fileSystemItem.getBasePath() + "/" + fileSystemItem.getName();

        if (avoidDuplicateMetaInfo) {
            log.debug("checking for meta-info for file {}...", fullPath);
            Optional<FileMetaInfo> fileMetaInfoByHash = retrieveMetaInfoByHash(fileSystemItem);
            if (fileMetaInfoByHash.isPresent()) {
                log.debug("found meta info by hash");
                return fileMetaInfoByHash;
            }
        }

        log.debug("generating new file meta info");
        return this.metaInfoExtractorStrategyList
                .stream()
                .filter(strategy -> strategy.accept(fullPath, fileSystemItem))
                .findFirst()
                .map(strategy -> strategy.extract(fullPath, fileSystemItem))
                .flatMap(fileMetaInfo -> fileMetaInfo)
                .map(this.fileMetaInfoRepository::saveAndFlush);

    }

    private Optional<FileMetaInfo> retrieveMetaInfoByHash(FileSystemItem fileSystemItem) {
        return this.fileSystemItemRepository.findBySha256(fileSystemItem.getSha256())
                .stream()
                .findFirst();
    }
}
