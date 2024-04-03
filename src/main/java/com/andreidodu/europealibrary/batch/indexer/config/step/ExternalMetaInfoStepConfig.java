package com.andreidodu.europealibrary.batch.indexer.config.step;

import com.andreidodu.europealibrary.batch.indexer.step.externalapi.ExternalMetaInfoProcessor;
import com.andreidodu.europealibrary.batch.indexer.step.externalapi.ExternalMetaInfoWriter;
import com.andreidodu.europealibrary.model.FileSystemItem;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;

@Configuration
@RequiredArgsConstructor
public class ExternalMetaInfoStepConfig {
    @Value("${com.andreidodu.europea-library.job.indexer.step-step-updater.batch-size}")
    private Integer batchSize;

    private final JobRepository jobRepository;
    private final JdbcPagingItemReader<Long> metaInfoBuilderReader;
    private final ExternalMetaInfoProcessor processor;
    private final ExternalMetaInfoWriter metaInfoWriter;
    private final HibernateTransactionManager transactionManager;

    @Bean("externalMetaInfoBuilderStep")
    public Step externalMetaInfoBuilderStep() {
        return new StepBuilder("externalMetaInfoBuilderStep", jobRepository)
                .<Long, FileSystemItem>chunk(batchSize, transactionManager)
                .allowStartIfComplete(true)
                .reader(metaInfoBuilderReader)
                .processor(processor)
                .writer(metaInfoWriter)
                .build();
    }
}
