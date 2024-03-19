package com.andreidodu.europealibrary.batch.indexer.step.file;

import com.andreidodu.europealibrary.client.OpenLibraryClient;
import com.andreidodu.europealibrary.client.OpenLibraryConfiguration;
import com.andreidodu.europealibrary.dto.OpenLibraryAuthenticationRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
public class LoginEbookServiceListener implements StepExecutionListener {
    @Autowired
    @Qualifier("openLibraryClient")
    private OpenLibraryClient openLibraryClient;
    @Autowired
    private OpenLibraryConfiguration openLibraryConfiguration;

    @Override
    public void beforeStep(final org.springframework.batch.core.StepExecution stepExecution) {
        log.info("Before Step Start Time{}", Instant.now());
        String session = openLibraryClient.authenticate(new OpenLibraryAuthenticationRequestDTO(openLibraryConfiguration.getAccess(), openLibraryConfiguration.getSecret()));
        stepExecution.getExecutionContext().put("session", session);
    }

    @Override
    public ExitStatus afterStep(final org.springframework.batch.core.StepExecution stepExecution) {
        log.info("After Step End Time{}", Instant.now());
        return null;
    }
}
