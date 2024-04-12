package com.andreidodu.europealibrary.batch.indexer.step.finalization;

import com.andreidodu.europealibrary.service.CacheLoader;
import com.andreidodu.europealibrary.service.CursoredFileSystemService;
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
    private final CursoredFileSystemService cursoredFileSystemService;
    private final CacheLoader cacheLoader;

    public RepeatStatus execute(StepContribution contribution,
                                ChunkContext chunkContext) throws Exception {
        clearAndReloadCache();
        return RepeatStatus.FINISHED;
    }

    private void clearAndReloadCache() {
        log.debug("loading data in cache");
        this.cacheLoader.reload();
        log.debug("cache loaded");
    }

}
