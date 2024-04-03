package com.andreidodu.europealibrary.batch.indexer.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JobFlowConfig {
    public static final String STEP_STATUS_COMPLETED = "COMPLETED";

    @Bean("indexerJob")
    public Job indexerJob(JobRepository jobRepository, Step fileSystemItemHashStep, Step metaInfoBuilderStep, Step externalMetaInfoBuilderStep, Step fileIndexerAndCataloguerStep, Step dbFSIObsoleteDeleterStep, Step dbFMIObsoleteDeleterStep, Step dbJobStepUpdaterStep) {
        return new JobBuilder("indexerJob", jobRepository)
                .start(fileIndexerAndCataloguerStep)
                .on(STEP_STATUS_COMPLETED).to(fileSystemItemHashStep)
                .on(STEP_STATUS_COMPLETED).to(metaInfoBuilderStep)
                .on(STEP_STATUS_COMPLETED).to(externalMetaInfoBuilderStep)
                .on(STEP_STATUS_COMPLETED).to(dbFSIObsoleteDeleterStep)
                .on(STEP_STATUS_COMPLETED).to(dbFMIObsoleteDeleterStep)
                .on(STEP_STATUS_COMPLETED).to(dbJobStepUpdaterStep)
                .end()
                .build();
    }

}
