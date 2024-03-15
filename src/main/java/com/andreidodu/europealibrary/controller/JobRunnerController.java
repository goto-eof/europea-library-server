package com.andreidodu.europealibrary.controller;

import com.andreidodu.europealibrary.dto.JobStatusDTO;
import com.andreidodu.europealibrary.service.JobRunnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/job")
@RequiredArgsConstructor
public class JobRunnerController {
    final private JobRunnerService jobRunnerService;

    @RequestMapping("/indexer/run")
    public ResponseEntity<JobStatusDTO> handle() throws Exception {
        jobRunnerService.runJobAsync();
        return ResponseEntity.ok(buildJobStartedResponse());
    }

    private static JobStatusDTO buildJobStartedResponse() {
        return JobStatusDTO.builder()
                .message("Job started")
                .isRunning(true)
                .build();
    }
}
