package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.service.JobRunnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class JobRunnerServiceImpl implements JobRunnerService {
    final private JobLauncher jobLauncher;
    final private Job job;

    @Override
    @Async
    public void runJobAsync() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
        try {   /*new JobParametersBuilder().addDate("date", new Date(System.currentTimeMillis())).toJobParameters()*/
            jobLauncher.run(job, new JobParameters());
        } catch (JobExecutionAlreadyRunningException e) {
            log.warn("Job is already running: {}", e.getMessage());
        } catch (RuntimeException e) {
            log.error("Error starting job: {}", e.getMessage());
        }
    }
}
