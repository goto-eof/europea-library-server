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
    public static final String DO_NOT_CALL_WEB_API = "do-not-call-web-api";
    public static final int SLEEP_TIME_BETWEEN_API_REQUESTS = 1000;

    final private EpubUtil epubUtil;
    final private List<MetaInfoExtractorStrategy> metaInfoExtractorStrategyList;
    final private List<MetaInfoRetrieverStrategy> metaInfoRetrieverStrategyList;
    private final FileMetaInfoRepository fileMetaInfoRepository;
    private StepExecution stepExecution;
    final private FileSystemItemRepository fileSystemItemRepository;

    @BeforeStep
    public void setStepExecution(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public FileSystemItem process(Long fileSystemItemId) {
        return Optional.ofNullable(buildMetaInfoFromEbookIfNecessary(fileSystemItemId))
                .map(fileMetaInfo -> {
                    FileSystemItem fileSystemItem = this.fileSystemItemRepository.findById(fileSystemItemId).get();
                    fileSystemItem.setFileMetaInfoId(fileMetaInfo.getId());
                    return fileSystemItem;
                })
                .orElse(null);
        //   FileSystemItem savedFileSystemItem = this.fileSystemItemRepository.save(fileSystemItem);
//        buildMetaInfoFromWebIfNecessary(savedFileSystemItem);
//        FileSystemItem saved2FileSystemItem = this.fileSystemItemRepository.save(fileSystemItem);
//        return Optional.ofNullable(fileSystemItem.getFileMetaInfo()).map(fileMetaInfo ->
//        {
//            if (fileMetaInfo.getFileSystemItemList() == null) {
//                fileMetaInfo.setFileSystemItemList(new ArrayList<>());
//            }
//            if (!fileMetaInfo.getFileSystemItemList().contains(saved2FileSystemItem)) {
//                fileMetaInfo.getFileSystemItemList().add(saved2FileSystemItem);
//            }
//            return fileMetaInfo;
//        }).orElse(null);
    }

    private FileMetaInfo buildMetaInfoFromEbookIfNecessary(Long fileSystemItemId) {
        FileSystemItem fileSystemItem = this.fileSystemItemRepository.findById(fileSystemItemId).get();
        String fullPath = fileSystemItem.getBasePath() + "/" + fileSystemItem.getName();
        log.info("checking for meta-info for file {}...", fullPath);
        return this.metaInfoExtractorStrategyList
                .stream()
                .filter(strategy -> strategy.accept(fullPath, fileSystemItem))
                .findFirst()
                .map(strategy -> strategy.extract(fullPath, fileSystemItem))
                .flatMap(fileMetaInfo -> fileMetaInfo)
                .map(this.fileMetaInfoRepository::save)
                .orElse(null);

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
