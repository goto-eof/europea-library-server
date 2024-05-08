package com.andreidodu.europealibrary.batch.indexer.step.finalization;

import com.andreidodu.europealibrary.batch.indexer.util.TaskletUtil;
import com.andreidodu.europealibrary.batch.indexer.constants.JobConst;
import com.andreidodu.europealibrary.service.ApplicationSettingsService;
import com.andreidodu.europealibrary.service.CacheLoaderService;
import com.andreidodu.europealibrary.service.CursoredFileSystemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FinalizationTasklet implements Tasklet {
    private final ApplicationSettingsService applicationSettingsService;
    private final CursoredFileSystemService cursoredFileSystemService;
    private final CacheLoaderService cacheLoaderService;
    private final TaskletUtil taskletUtil;

    public RepeatStatus execute(StepContribution contribution,
                                ChunkContext chunkContext) {
        clearAndReloadCache();
        this.applicationSettingsService.unlockApplication();

        restoreFeaturedFileSystemItemId(chunkContext);

        return RepeatStatus.FINISHED;
    }

    private void restoreFeaturedFileSystemItemId(ChunkContext chunkContext) {
        ExecutionContext executionContext = this.taskletUtil.getExecutionContext(chunkContext);
        if (executionContext.containsKey(JobConst.JOB_VARIABLE_FEATURED_FSI_ID)) {
            Long featuredFileSystemItemId = executionContext.getLong(JobConst.JOB_VARIABLE_FEATURED_FSI_ID);
            this.applicationSettingsService.setFeatured(featuredFileSystemItemId);
        }
    }

    private void clearAndReloadCache() {
        log.debug("loading data in cache");
        this.cacheLoaderService.reload();
        log.debug("cache loaded");
    }

}
