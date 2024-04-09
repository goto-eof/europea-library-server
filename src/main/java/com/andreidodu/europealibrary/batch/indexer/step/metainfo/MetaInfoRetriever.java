package com.andreidodu.europealibrary.batch.indexer.step.metainfo;

import com.andreidodu.europealibrary.batch.indexer.step.metainfo.dataextractor.MetaInfoExtractorStrategy;
import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.FileMetaInfoRepository;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Optional<FileMetaInfo> buildMetaInfoFromEbookIfNecessary(FileSystemItem fileSystemItem) {
        Optional<FileMetaInfo> fileMetaInfoByHash = isAssociateMetaInfoIfExists(fileSystemItem);
        if (fileMetaInfoByHash.isPresent()) {
            return fileMetaInfoByHash;
        }

        String fullPath = fileSystemItem.getBasePath() + "/" + fileSystemItem.getName();
        log.debug("checking for meta-info for file {}...", fullPath);
        return this.metaInfoExtractorStrategyList
                .stream()
                .filter(strategy -> strategy.accept(fullPath, fileSystemItem))
                .findFirst()
                .map(strategy -> strategy.extract(fullPath, fileSystemItem))
                .flatMap(fileMetaInfo -> fileMetaInfo)
                .map(this.fileMetaInfoRepository::saveAndFlush);

    }

    private Optional<FileMetaInfo> isAssociateMetaInfoIfExists(FileSystemItem fileSystemItem) {
        return this.fileSystemItemRepository.findBySha256(fileSystemItem.getSha256())
                .stream()
                .filter(fsi -> fsi.getFileMetaInfo() != null)
                .findFirst()
                .flatMap(fsi -> Optional.ofNullable(fileSystemItem.getFileMetaInfo()));
    }
}
