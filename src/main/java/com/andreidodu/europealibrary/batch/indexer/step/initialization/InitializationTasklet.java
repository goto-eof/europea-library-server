package com.andreidodu.europealibrary.batch.indexer.step.initialization;

import com.andreidodu.europealibrary.service.ApplicationSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitializationTasklet implements Tasklet {
    private final ApplicationSettingsService applicationSettingsService;

    @Value("${com.andreidodu.europea-library.job.indexer.e-books-directory}")
    private String ebooksDirectory;

    public RepeatStatus execute(StepContribution contribution,
                                ChunkContext chunkContext) {

        log.info("the e-books directory is [{}]", this.ebooksDirectory);

        this.applicationSettingsService.lockApplication();

        return RepeatStatus.FINISHED;
    }

}
