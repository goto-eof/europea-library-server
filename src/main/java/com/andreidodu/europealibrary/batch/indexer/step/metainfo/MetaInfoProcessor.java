package com.andreidodu.europealibrary.batch.indexer.step.metainfo;

import com.andreidodu.europealibrary.batch.indexer.step.metainfo.dataextractor.MetaInfoExtractorStrategy;
import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.FileMetaInfoRepository;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MetaInfoProcessor implements ItemProcessor<Long, FileSystemItem> {

    final private List<MetaInfoExtractorStrategy> metaInfoExtractorStrategyList;
    final private FileMetaInfoRepository fileMetaInfoRepository;
    final private FileSystemItemRepository fileSystemItemRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public FileSystemItem process(Long fileSystemItemId) {
        FileSystemItem fileSystemItem = this.fileSystemItemRepository.findById(fileSystemItemId).get();
        return buildMetaInfoFromEbookIfNecessary(fileSystemItem)
                .map(fileMetaInfo -> {
                    this.entityManager.detach(fileSystemItem);
                    fileSystemItem.setFileMetaInfoId(fileMetaInfo.getId());
                    return fileSystemItem;
                })
                .orElse(null);
    }

    private Optional<FileMetaInfo> buildMetaInfoFromEbookIfNecessary(FileSystemItem fileSystemItem) {
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
                .map(this.fileMetaInfoRepository::save);

    }

    private Optional<FileMetaInfo> isAssociateMetaInfoIfExists(FileSystemItem fileSystemItem) {
        return this.fileSystemItemRepository.findBySha256(fileSystemItem.getSha256())
                .stream()
                .filter(fsi -> fsi.getFileMetaInfo() != null)
                .findFirst()
                .flatMap(fsi -> Optional.ofNullable(fileSystemItem.getFileMetaInfo()));
    }

}
