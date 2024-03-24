package com.andreidodu.europealibrary.batch.indexer;

import com.andreidodu.europealibrary.batch.indexer.step.dbfmiobsoletedeleter.DbFMIObsoleteDeleterProcessor;
import com.andreidodu.europealibrary.batch.indexer.step.dbfmiobsoletedeleter.DbFMIObsoleteDeleterWriter;
import com.andreidodu.europealibrary.batch.indexer.step.dbfsiobsoletedeleter.DbFSIObsoleteDeleterProcessor;
import com.andreidodu.europealibrary.batch.indexer.step.dbfsiobsoletedeleter.DbFSIObsoleteDeleterWriter;
import com.andreidodu.europealibrary.batch.indexer.step.dbstepupdater.DbStepUpdaterProcessor;
import com.andreidodu.europealibrary.batch.indexer.step.dbstepupdater.DbStepUpdaterWriter;
import com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer.FileIndexerProcessor;
import com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer.FileIndexerReader;
import com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer.FileIndexerWriter;
import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.FileSystemItem;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class JobConfiguration {
    @Value("${com.andreidodu.europea-library.job.indexer.step-indexer.batch-size}")
    private Integer stepIndexerBatchSize;

    @Value("${com.andreidodu.europea-library.job.indexer.step-step-updater.batch-size}")
    private Integer stepStepUpdaterBatchSize;
    @Value("${com.andreidodu.europea-library.job.indexer.step-fmi-obsolete-deleter.batch-size}")
    private Integer stepFmiObsoleteDeleterBatchSize;

    @Value("${com.andreidodu.europea-library.job.indexer.step-fsi-obsolete-deleter.batch-size}")
    private Integer stepFsiObsoleteDeleterBatchSize;

    final private EntityManagerFactory emFactory;

    @Bean("indexerJob")
    public Job indexerJob(JobRepository jobRepository, Step fileIndexerAndCataloguerStep, Step dbFSIObsoleteDeleterStep, Step dbFMIObsoleteDeleterStep, Step dbJobStepUpdaterStep) {
        return new JobBuilder("indexerJob", jobRepository)
                .start(fileIndexerAndCataloguerStep)
                .next(dbFSIObsoleteDeleterStep)
                .next(dbFMIObsoleteDeleterStep)
                .next(dbJobStepUpdaterStep)
                .build();
    }

    @Bean("fileIndexerAndCataloguerStep")
    public Step fileIndexerAndCataloguerStep(JobRepository jobRepository, FileIndexerProcessor processor, FileIndexerReader fileIndexerReader, FileIndexerWriter writer, HibernateTransactionManager transactionManager) {
        return new StepBuilder("indexDirectoriesAndFiles", jobRepository)
                .<File, FileSystemItem>chunk(stepIndexerBatchSize, transactionManager)
                .reader(fileIndexerReader)
                .allowStartIfComplete(true)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean("dbFSIObsoleteDeleterStep")
    public Step dbFSIObsoleteDeleterStep(JpaCursorItemReader<FileSystemItem> dbFSIObsoleteDeleterReader, JobRepository jobRepository, DbFSIObsoleteDeleterProcessor processor, DbFSIObsoleteDeleterWriter fileItemWriter, HibernateTransactionManager transactionManager) {
        return new StepBuilder("deleteDbFilesStep", jobRepository)
                .<FileSystemItem, FileSystemItem>chunk(stepFsiObsoleteDeleterBatchSize, transactionManager)
                .allowStartIfComplete(true)
                .reader(dbFSIObsoleteDeleterReader)
                .processor(processor)
                .writer(fileItemWriter)
                .build();
    }

    @Bean("dbFMIObsoleteDeleterStep")
    public Step dbFMIObsoleteDeleterStep(JpaCursorItemReader<FileMetaInfo> dbFMIObsoleteDeleterReader, JobRepository jobRepository, DbFMIObsoleteDeleterProcessor processor, DbFMIObsoleteDeleterWriter fileItemWriter, HibernateTransactionManager transactionManager) {
        return new StepBuilder("deleteDbFilesStep", jobRepository)
                .<FileMetaInfo, FileMetaInfo>chunk(stepFmiObsoleteDeleterBatchSize, transactionManager)
                .allowStartIfComplete(true)
                .reader(dbFMIObsoleteDeleterReader)
                .processor(processor)
                .writer(fileItemWriter)
                .build();
    }

    @Bean("dbJobStepUpdaterStep")
    public Step dbJobStepUpdaterStep(JobRepository jobRepository, JpaCursorItemReader<FileSystemItem> dbStepUpdaterReader, DbStepUpdaterProcessor processor, DbStepUpdaterWriter dbStepUpdaterWriter, HibernateTransactionManager transactionManager) {
        return new StepBuilder("finalizeJobStep", jobRepository)
                .<FileSystemItem, FileSystemItem>chunk(stepStepUpdaterBatchSize, transactionManager)
                .allowStartIfComplete(true)
                .reader(dbStepUpdaterReader)
                .processor(processor)
                .writer(dbStepUpdaterWriter)
                .build();
    }

    @Bean("dbFSIObsoleteDeleterReader")
    public JpaCursorItemReader<FileSystemItem> dbFSIObsoleteDeleterReader() {
        JpaCursorItemReader<FileSystemItem> jpaCursorItemReader = new JpaCursorItemReader<FileSystemItem>();
        jpaCursorItemReader.setEntityManagerFactory(emFactory);
        jpaCursorItemReader.setQueryString("SELECT p FROM FileSystemItem p where recordStatus = :recordStatus");
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("recordStatus", RecordStatusEnum.ENABLED.getStatus());
        jpaCursorItemReader.setParameterValues(parameterValues);
        jpaCursorItemReader.setSaveState(true);
        jpaCursorItemReader.setMaxItemCount(100);
        return jpaCursorItemReader;
    }

    @Bean("dbFMIObsoleteDeleterReader")
    public JpaCursorItemReader<FileMetaInfo> dbFMIObsoleteDeleterReader() {
        JpaCursorItemReader<FileMetaInfo> jpaCursorItemReader = (new JpaCursorItemReader<FileMetaInfo>());
        jpaCursorItemReader.setEntityManagerFactory(emFactory);
        jpaCursorItemReader.setQueryString("SELECT p FROM FileMetaInfo p where p.fileSystemItemList IS EMPTY");
        jpaCursorItemReader.setSaveState(true);
        jpaCursorItemReader.setMaxItemCount(100);
        return jpaCursorItemReader;
    }

    @Bean("dbStepUpdaterReader")
    public JpaCursorItemReader<FileSystemItem> dbStepUpdaterReader() {
        JpaCursorItemReader<FileSystemItem> jpaCursorItemReader = (new JpaCursorItemReader<FileSystemItem>());
        jpaCursorItemReader.setEntityManagerFactory(emFactory);
        jpaCursorItemReader.setQueryString("SELECT p FROM FileSystemItem p where recordStatus = :recordStatus");
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("recordStatus", RecordStatusEnum.JUST_UPDATED.getStatus());
        jpaCursorItemReader.setParameterValues(parameterValues);
        jpaCursorItemReader.setSaveState(true);
        jpaCursorItemReader.setMaxItemCount(100);
        return jpaCursorItemReader;
    }

}
