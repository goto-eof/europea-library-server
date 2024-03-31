package com.andreidodu.europealibrary.batch.indexer.step.metainfo;

import com.andreidodu.europealibrary.batch.indexer.enums.ApiStatusEnum;
import com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer.dataextractor.MetaInfoExtractorStrategy;
import com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer.dataretriever.MetaInfoRetrieverStrategy;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MetaInfoProcessor implements ItemProcessor<FileSystemItem, FileMetaInfo> {
    public static final String DO_NOT_CALL_WEB_API = "do-not-call-web-api";
    public static final int SLEEP_TIME_BETWEEN_API_REQUESTS = 1000;

    final private EpubUtil epubUtil;
    final private List<MetaInfoExtractorStrategy> metaInfoExtractorStrategyList;
    final private List<MetaInfoRetrieverStrategy> metaInfoRetrieverStrategyList;
    private StepExecution stepExecution;
    final private FileSystemItemRepository fileSystemItemRepository;

    @BeforeStep
    public void setStepExecution(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public FileMetaInfo process(FileSystemItem fileSystemItem) {
        buildMetaInfoFromEbookIfNecessary(fileSystemItem);
        buildMetaInfoFromWebIfNecessary(fileSystemItem);

        return Optional.ofNullable(fileSystemItem.getFileMetaInfo()).map(fileMetaInfo ->
        {
            if (fileMetaInfo.getFileSystemItemList() == null) {
                fileMetaInfo.setFileSystemItemList(new ArrayList<>());
            }
            if (!fileMetaInfo.getFileSystemItemList().contains(fileSystemItem)) {
                fileMetaInfo.getFileSystemItemList().add(fileSystemItem);
            }
            return fileMetaInfo;
        }).orElse(null);
    }

    private void buildMetaInfoFromWebIfNecessary(FileSystemItem fileSystemItem) {
        metaInfoRetrieverStrategyList.stream()
                .filter(strategy -> strategy.accept(fileSystemItem))
                .findFirst()
                .ifPresent(strategy -> {
                    if (doNotCallApiIsTrue()) {
                        return;
                    }
                    ApiStatusEnum result = strategy.process(fileSystemItem);
                    if (result == ApiStatusEnum.FATAL_ERROR) {
                        this.stepExecution.getExecutionContext().put(DO_NOT_CALL_WEB_API, true);
                        return;
                    }
                    // putThreadOnSleep();
                });
    }

    private void buildMetaInfoFromEbookIfNecessary(FileSystemItem fileSystemItem) {
        String fullPath = fileSystemItem.getBasePath() + "/" + fileSystemItem.getName();
        log.info("checking for meta-info for file {}...", fullPath);
        this.metaInfoExtractorStrategyList
                .stream()
                .filter(strategy -> strategy.accept(fullPath, fileSystemItem))
                .findFirst()
                .map(strategy -> strategy.extract(fullPath, fileSystemItem))
                .flatMap(fileMetaInfo -> fileMetaInfo)
                .ifPresent(fileSystemItem::setFileMetaInfo);
    }

    private boolean doNotCallApiIsTrue() {
        Object doNotCallApiIdTrue = stepExecution.getExecutionContext().get(DO_NOT_CALL_WEB_API);
        return doNotCallApiIdTrue != null && (boolean) doNotCallApiIdTrue;
    }

    private static void putThreadOnSleep() {
        try {
            Thread.sleep(SLEEP_TIME_BETWEEN_API_REQUESTS);
        } catch (InterruptedException e) {
            log.error("failed to put thread in sleep mode");
        }
    }
}
