package com.andreidodu.europealibrary.batch.indexer.step.externalapi;

import com.andreidodu.europealibrary.batch.indexer.enums.ApiStatusEnum;
import com.andreidodu.europealibrary.batch.indexer.step.externalapi.dataretriever.MetaInfoRetrieverStrategy;
import com.andreidodu.europealibrary.dto.ApiResponseDTO;
import com.andreidodu.europealibrary.exception.SkipStepException;
import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalMetaInfoProcessor implements ItemProcessor<Long, FileSystemItem> {
    public static final int SLEEP_TIME_BETWEEN_API_REQUESTS = 1000;

    final private List<MetaInfoRetrieverStrategy> metaInfoRetrieverStrategyList;
    final private FileSystemItemRepository fileSystemItemRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public FileSystemItem process(Long fileSystemItemId) {
        FileSystemItem fileSystemItem = this.fileSystemItemRepository.findById(fileSystemItemId).get();
        FileMetaInfo fileMetaInfo = buildMetaInfoFromWebIfNecessary(fileSystemItem);
        this.entityManager.detach(fileSystemItem);
        fileSystemItem.setFileMetaInfoId(fileMetaInfo.getId());
        log.debug("external meta-info retrieved: {}", fileSystemItem.getName());
        putThreadOnSleep();
        return fileSystemItem;
    }

    private FileMetaInfo buildMetaInfoFromWebIfNecessary(FileSystemItem fileSystemItem) {
        return metaInfoRetrieverStrategyList.stream()
                .filter(strategy -> strategy.accept(fileSystemItem))
                .findFirst()
                .map(metaInfoRetrieverStrategy -> metaInfoRetrieverStrategy.process(fileSystemItem))
                .filter(result -> List.of(ApiStatusEnum.SUCCESS, ApiStatusEnum.SUCCESS_EMPTY_RESPONSE).contains(result.getStatus()))
                .map(ApiResponseDTO::getEntity)
                .orElseThrow(() -> new SkipStepException("step was skipped because google books api returned an error"));

    }

    private static void putThreadOnSleep() {
        try {
            Thread.sleep(SLEEP_TIME_BETWEEN_API_REQUESTS);
        } catch (InterruptedException e) {
            log.error("failed to put thread in sleep mode");
        }
    }
}
