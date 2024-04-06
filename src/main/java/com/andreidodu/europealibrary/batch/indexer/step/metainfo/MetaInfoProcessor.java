package com.andreidodu.europealibrary.batch.indexer.step.metainfo;

import com.andreidodu.europealibrary.batch.indexer.step.metainfo.dataextractor.MetaInfoExtractorStrategy;
import com.andreidodu.europealibrary.batch.indexer.step.externalapi.dataretriever.MetaInfoRetrieverStrategy;
import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.FileMetaInfoRepository;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import com.andreidodu.europealibrary.util.EpubUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
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

    @Override
    public FileSystemItem process(Long fileSystemItemId) {
        return Optional.ofNullable(buildMetaInfoFromEbookIfNecessary(fileSystemItemId))
                .map(fileMetaInfo -> {
                    FileSystemItem fileSystemItem = this.fileSystemItemRepository.findById(fileSystemItemId).get();
                    fileSystemItem.setFileMetaInfoId(fileMetaInfo.getId());
                    return fileSystemItem;
                })
                .orElse(null);
    }

    private FileMetaInfo buildMetaInfoFromEbookIfNecessary(Long fileSystemItemId) {
        FileSystemItem fileSystemItem = this.fileSystemItemRepository.findById(fileSystemItemId).get();
        String fullPath = fileSystemItem.getBasePath() + "/" + fileSystemItem.getName();
        log.debug("checking for meta-info for file {}...", fullPath);
        return this.metaInfoExtractorStrategyList
                .stream()
                .filter(strategy -> strategy.accept(fullPath, fileSystemItem))
                .findFirst()
                .map(strategy -> strategy.extract(fullPath, fileSystemItem))
                .flatMap(fileMetaInfo -> fileMetaInfo)
                .map(this.fileMetaInfoRepository::save)
                .orElse(null);

    }

}
