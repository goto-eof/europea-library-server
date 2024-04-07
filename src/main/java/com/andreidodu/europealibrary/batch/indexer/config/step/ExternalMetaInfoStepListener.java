package com.andreidodu.europealibrary.batch.indexer.config.step;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExternalMetaInfoStepListener implements StepExecutionListener {
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        stepExecution.setStatus(BatchStatus.COMPLETED);
        return ExitStatus.COMPLETED;
    }
}
