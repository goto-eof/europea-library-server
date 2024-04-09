package com.andreidodu.europealibrary.batch.indexer.config.step;

import com.andreidodu.europealibrary.batch.indexer.step.finalization.FinalizationTasklet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FinalizationStepConfig {
    private final FinalizationTasklet finalizationTasklet;

    @Bean
    public Step finalizationStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("finalizationStep", jobRepository)
                .allowStartIfComplete(true)
                .tasklet(finalizationTasklet, transactionManager)
                .build();
    }
}
