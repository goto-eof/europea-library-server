package com.andreidodu.europealibrary.batch.indexer.step.externalapi;

import com.andreidodu.europealibrary.batch.indexer.enums.ApiStatusEnum;
import com.andreidodu.europealibrary.batch.indexer.step.externalapi.dataretriever.MetaInfoRetrieverStrategy;
import com.andreidodu.europealibrary.dto.ApiResponseDTO;
import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
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
public class ExternalMetaInfoProcessor implements ItemProcessor<Long, FileSystemItem> {
    public static final String DO_NOT_CALL_WEB_API = "do-not-call-web-api";
    public static final int SLEEP_TIME_BETWEEN_API_REQUESTS = 1000;

    final private List<MetaInfoRetrieverStrategy> metaInfoRetrieverStrategyList;
    private StepExecution stepExecution;
    final private FileSystemItemRepository fileSystemItemRepository;

    @BeforeStep
    public void setStepExecution(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public FileSystemItem process(Long fileSystemItemId) {
        return buildMetaInfoFromWebIfNecessary(fileSystemItemId)
                .map(fileMetaInfo -> {
                    FileSystemItem fileSystemItem = this.fileSystemItemRepository.findById(fileSystemItemId).get();
                    fileSystemItem.setFileMetaInfoId(fileMetaInfo.getId());
                    putThreadOnSleep();
                    return fileSystemItem;
                })
                .orElse(null);
    }

    private Optional<FileMetaInfo> buildMetaInfoFromWebIfNecessary(Long fileSystemItemId) {
        FileSystemItem fileSystemItem = this.fileSystemItemRepository.findById(fileSystemItemId).get();
        return metaInfoRetrieverStrategyList.stream()
                .filter(strategy -> strategy.accept(fileSystemItem))
                .findFirst()
                .map(strategy -> {
                    if (doNotCallApiIsTrue()) {
                        return null;
                    }
                    ApiResponseDTO<FileMetaInfo> result = strategy.process(fileSystemItem);
                    if (result.getStatus() == ApiStatusEnum.FATAL_ERROR) {
                        this.stepExecution.getExecutionContext().put(DO_NOT_CALL_WEB_API, true);
                        return result.getEntity();
                    }
                    return result.getEntity();
                });
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
