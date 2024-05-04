package com.andreidodu.europealibrary.resource.impl;

import com.andreidodu.europealibrary.annotation.auth.AllowOnlyAdministrator;
import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import com.andreidodu.europealibrary.resource.JobRunnerResource;
import com.andreidodu.europealibrary.service.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class JobRunnerResourceImpl implements JobRunnerResource {
    final private JobService jobService;

    @Override
    public ResponseEntity<OperationStatusDTO> run() throws Exception {
        jobService.runJobAsync();
        return ResponseEntity.ok(buildJobStartedResponse());
    }

    @Override
    public ResponseEntity<OperationStatusDTO> isRunning() {
        return ResponseEntity.ok(this.jobService.isRunning());
    }

    @Override
    public ResponseEntity<OperationStatusDTO> stop() {
        return ResponseEntity.ok(this.jobService.stop());
    }

    private static OperationStatusDTO buildJobStartedResponse() {
        return OperationStatusDTO.builder()
                .message("Job started")
                .status(true)
                .build();
    }
}
