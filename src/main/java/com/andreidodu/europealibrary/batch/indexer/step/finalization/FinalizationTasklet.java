package com.andreidodu.europealibrary.batch.indexer.step.finalization;

import com.andreidodu.europealibrary.service.ApplicationSettingsService;
import com.andreidodu.europealibrary.service.CacheLoaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FinalizationTasklet implements Tasklet {
    private final ApplicationSettingsService applicationSettingsService;
    private final CacheLoaderService cacheLoaderService;

    public RepeatStatus execute(StepContribution contribution,
                                ChunkContext chunkContext) {
        clearAndReloadCache();
        this.applicationSettingsService.unlockApplication();
        return RepeatStatus.FINISHED;
    }

    private void clearAndReloadCache() {
        log.debug("loading data in cache");
        this.cacheLoaderService.reload();
        log.debug("cache loaded");
    }

}
