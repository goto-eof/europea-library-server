package com.andreidodu.europealibrary.batch.indexer.util;

import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

@Component
public class TaskletUtil {

    public ExecutionContext getExecutionContext(ChunkContext chunkContext) {
        return chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext();
    }

}
