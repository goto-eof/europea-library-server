package com.andreidodu.europealibrary.batch.indexer.config.listener;

import com.andreidodu.europealibrary.service.ApplicationSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IndexerJobExecutionListener implements JobExecutionListener {
    private final ApplicationSettingsService applicationSettingsService;

    @Override
    public void afterJob(JobExecution jobExecution) {
        this.applicationSettingsService.unlockApplication();
    }
}
