package com.andreidodu.europealibrary.config;

import com.andreidodu.europealibrary.repository.impl.JobRepositoryCustom;
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
    final private JobRepositoryCustom jobRepositoryCustom;

    @PostConstruct
    private void resetJobStatus() {
        jobRepositoryCustom.resetJobs();
    }
}
