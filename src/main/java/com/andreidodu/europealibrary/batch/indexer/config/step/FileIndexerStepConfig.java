package com.andreidodu.europealibrary.batch.indexer.config.step;

import com.andreidodu.europealibrary.batch.indexer.step.fileindexer.FileIndexerBulkWriter;
import com.andreidodu.europealibrary.batch.indexer.step.fileindexer.FileIndexerProcessor;
import com.andreidodu.europealibrary.batch.indexer.step.fileindexer.FileIndexerReader;
import com.andreidodu.europealibrary.model.FileSystemItem;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.File;

@Configuration
@RequiredArgsConstructor
public class FileIndexerStepConfig {
    private final FileIndexerProcessor processor;
    private final FileIndexerReader fileIndexerReader;
    private final FileIndexerBulkWriter writer;
    private final JobRepository jobRepository;
    private final HibernateTransactionManager transactionManager;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Value("${com.andreidodu.europea-library.job.indexer.step-indexer.batch-size}")
    private Integer stepIndexerBatchSize;

    @Bean("fileIndexerAndCataloguerStep")
    public Step fileIndexerAndCataloguerStep() {
        return new StepBuilder("indexDirectoriesAndFiles", jobRepository)
                .<File, FileSystemItem>chunk(stepIndexerBatchSize, transactionManager)
                .allowStartIfComplete(true)
                .taskExecutor(threadPoolTaskExecutor)
                .reader(fileIndexerReader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
