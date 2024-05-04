package com.andreidodu.europealibrary.resource;

import com.andreidodu.europealibrary.annotation.security.AllowOnlyAdministrator;
import com.andreidodu.europealibrary.dto.OperationStatusDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/job")
public interface JobRunnerResource {
    @AllowOnlyAdministrator
    @GetMapping("/indexer/run")
    ResponseEntity<OperationStatusDTO> run() throws Exception;

    @AllowOnlyAdministrator
    @GetMapping("/indexer/isRunning")
    ResponseEntity<OperationStatusDTO> isRunning();

    @AllowOnlyAdministrator
    @GetMapping("/indexer/stop")
    ResponseEntity<OperationStatusDTO> stop();
}
