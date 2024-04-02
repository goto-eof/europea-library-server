package com.andreidodu.europealibrary.batch.indexer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class IndexDirectoriesTaskScheduler {
    public static final String JOB_PARAMETER_DATE_KEY = "date";
    private final JobLauncher jobLauncher;
    private final Job job;

    @Scheduled(cron = "${com.andreidodu.europea-library.task.indexer.cron.expression}")
    public void runIndexAndCatalogueTaskScheduler() throws JobInstanceAlreadyCompleteException, JobParametersInvalidException, JobRestartException {
        try {
            jobLauncher.run(job, new JobParameters());
        } catch (JobExecutionAlreadyRunningException e) {
            log.warn("Job is already running: {}", e.getMessage());
        } catch (RuntimeException e) {
            log.error("Error starting job: {}", e.getMessage());
        }
    }

    private static JobParameters generateJobParameters() {
        return new JobParametersBuilder().addDate(JOB_PARAMETER_DATE_KEY, new Date(System.currentTimeMillis())).toJobParameters();
    }
}
