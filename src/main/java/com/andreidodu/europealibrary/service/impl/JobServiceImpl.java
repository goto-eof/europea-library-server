package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.batch.indexer.constants.JobConst;
import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.service.ApplicationSettingsService;
import com.andreidodu.europealibrary.service.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Slf4j
@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobLauncher jobLauncher;
    private final JobExplorer jobExplorer;
    private final JobOperator jobOperator;
    private final ApplicationSettingsService applicationSettingsService;

    @Autowired
    @Qualifier("indexerJob")
    private Job job;

    @Override
    @Async
    public void runJobAsync() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
        try {
            jobLauncher.run(job, new JobParameters());
        } catch (JobExecutionAlreadyRunningException e) {
            log.warn("Job is already running: {}", e.getMessage());
        } catch (RuntimeException e) {
            log.error("Error starting job: {}", e.getMessage());
        }
    }

    @Override
    public OperationStatusDTO isRunning() {
        final boolean isLocked = this.applicationSettingsService.isApplicationLocked();
        String stepName = "";
        if (isLocked) {
            stepName = this.jobExplorer.findJobInstancesByJobName(JobConst.JOB_INDEXER_NAME, 0, 1000000)
                    .stream()
                    .map(jobInstance -> this.jobExplorer.getJobExecutions(jobInstance)
                            .stream()
                            .filter(Objects::nonNull)
                            .findFirst()
                            .map(jobExecution -> jobExecution.getStepExecutions()
                                    .stream()
                                    .filter(step -> step.getStatus().isRunning())
                                    .map(StepExecution::getStepName)
                                    .findFirst()
                                    .orElseGet(() -> ""))
                            .orElseGet(() -> ""))
                    .findFirst()
                    .orElseGet(() -> "");
        }
        return new OperationStatusDTO(isLocked, isLocked ? "Job is running. Current step: " + stepName : "job is not running");
    }

    @Override
    public OperationStatusDTO stop() {
        boolean result = this.jobExplorer.findJobInstancesByJobName(JobConst.JOB_INDEXER_NAME, 0, 1000000)
                .stream()
                .anyMatch(jobInstance -> this.jobExplorer.getJobExecutions(jobInstance)
                        .stream().filter(Objects::nonNull)
                        .anyMatch(this::stopJobIfIsRunning));
        this.applicationSettingsService.unlockApplication();
        return new OperationStatusDTO(result, result ? "Job stopped" : "Job not running");
    }

    private boolean stopJobIfIsRunning(JobExecution jobExecution) {
        try {
            if (jobExecution.isRunning()) {
                return this.jobOperator.stop(jobExecution.getId());
            }
            return false;
        } catch (NoSuchJobExecutionException | JobExecutionNotRunningException e) {
            throw new RuntimeException(e);
        }
    }
}
