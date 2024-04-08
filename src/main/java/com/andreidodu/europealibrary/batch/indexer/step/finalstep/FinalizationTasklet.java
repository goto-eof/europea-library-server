package com.andreidodu.europealibrary.batch.indexer.step.finalstep;

import com.andreidodu.europealibrary.constants.CacheConst;
import com.andreidodu.europealibrary.service.CursoredFileSystemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FinalizationTasklet implements Tasklet {
    private final CursoredFileSystemService cursoredFileSystemService;
    private final CacheManager cacheManager;

    public RepeatStatus execute(StepContribution contribution,
                                ChunkContext chunkContext) throws Exception {
        clearAndReloadFileExtensionsCache();
        return RepeatStatus.FINISHED;
    }

    private void clearAndReloadFileExtensionsCache() {
        log.debug("loading data in cache");
        Cache cache = cacheManager.getCache(CacheConst.CACHE_NAME_EXTENSIONS);
        if (cache != null) {
            cache.clear();
        }
        this.cursoredFileSystemService.getAllExtensions();
        log.debug("cache loaded");
    }

}
