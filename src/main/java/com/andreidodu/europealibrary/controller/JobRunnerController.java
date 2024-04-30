package com.andreidodu.europealibrary.controller;

import com.andreidodu.europealibrary.annotation.auth.AllowOnlyAdministrator;
import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.service.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/job")
@RequiredArgsConstructor
public class JobRunnerController {
    final private JobService jobService;

    private static OperationStatusDTO buildJobStartedResponse() {
        return OperationStatusDTO.builder()
                .message("Job started")
                .status(true)
                .build();
    }

    @AllowOnlyAdministrator
    @GetMapping("/indexer/run")
    public ResponseEntity<OperationStatusDTO> run() throws Exception {
        jobService.runJobAsync();
        return ResponseEntity.ok(buildJobStartedResponse());
    }

    @AllowOnlyAdministrator
    @GetMapping("/indexer/isRunning")
    public ResponseEntity<OperationStatusDTO> isRunning() {
        return ResponseEntity.ok(this.jobService.isRunning());
    }

    @AllowOnlyAdministrator
    @GetMapping("/indexer/stop")
    public ResponseEntity<OperationStatusDTO> stop() {
        return ResponseEntity.ok(this.jobService.stop());
    }
}
