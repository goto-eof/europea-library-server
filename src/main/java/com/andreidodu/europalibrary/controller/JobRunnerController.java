package com.andreidodu.europalibrary.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/job")
public class JobRunnerController {
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private Job job;

    @RequestMapping("/run")
    public void handle() throws Exception {
        jobLauncher.run(job, new JobParameters());
    }
}
