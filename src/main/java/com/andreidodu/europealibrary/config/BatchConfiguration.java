package com.andreidodu.europealibrary.config;

import com.andreidodu.europealibrary.repository.impl.CustomJobRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@Transactional
@RequiredArgsConstructor
public class BatchConfiguration {
    final private CustomJobRepository customJobRepository;

    @PostConstruct
    private void resetJobStatus() {
        customJobRepository.resetJobs();
    }
}
