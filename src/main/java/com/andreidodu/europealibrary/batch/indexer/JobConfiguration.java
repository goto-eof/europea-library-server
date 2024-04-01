package com.andreidodu.europealibrary.batch.indexer;

import com.andreidodu.europealibrary.batch.indexer.enums.RecordStatusEnum;
import com.andreidodu.europealibrary.batch.indexer.step.dbfmiobsoletedeleter.DbFMIObsoleteDeleterProcessor;
import com.andreidodu.europealibrary.batch.indexer.step.dbfmiobsoletedeleter.DbFMIObsoleteDeleterWriter;
import com.andreidodu.europealibrary.batch.indexer.step.dbfsiobsoletedeleter.DbFSIObsoleteDeleterProcessor;
import com.andreidodu.europealibrary.batch.indexer.step.dbfsiobsoletedeleter.DbFSIObsoleteDeleterWriter;
import com.andreidodu.europealibrary.batch.indexer.step.dbstepupdater.DbStepUpdaterProcessor;
import com.andreidodu.europealibrary.batch.indexer.step.dbstepupdater.DbStepUpdaterWriter;
import com.andreidodu.europealibrary.batch.indexer.step.externalapi.ExternalMetaInfoProcessor;
import com.andreidodu.europealibrary.batch.indexer.step.externalapi.ExternalMetaInfoWriter;
import com.andreidodu.europealibrary.batch.indexer.step.filehash.FileSystemItemHashProcessor;
import com.andreidodu.europealibrary.batch.indexer.step.filehash.FileSystemItemHashWriter;
import com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer.FileIndexerProcessor;
import com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer.FileIndexerReader;
import com.andreidodu.europealibrary.batch.indexer.step.fileindexerandcataloguer.FileIndexerWriter;
import com.andreidodu.europealibrary.batch.indexer.step.metainfo.MetaInfoProcessor;
import com.andreidodu.europealibrary.batch.indexer.step.metainfo.MetaInfoWriter;
import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.FileSystemItem;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JobConfiguration {
    public static final String STEP_STATUS_COMPLETED = "COMPLETED";
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
        return new StepBuilder("dbFSIObsoleteDeleterStep", jobRepository)
                .<FileSystemItem, FileSystemItem>chunk(stepFsiObsoleteDeleterBatchSize, transactionManager)
                .allowStartIfComplete(true)
                .reader(dbFSIObsoleteDeleterReader)
                .processor(processor)
                .writer(fileItemWriter)
                .build();
    }

    @Bean("dbFMIObsoleteDeleterStep")
    public Step dbFMIObsoleteDeleterStep(JdbcPagingItemReader<Long> dbFMIObsoleteDeleterReader, JobRepository jobRepository, DbFMIObsoleteDeleterProcessor processor, DbFMIObsoleteDeleterWriter fileItemWriter, HibernateTransactionManager transactionManager) {
        return new StepBuilder("dbFMIObsoleteDeleterStep", jobRepository)
                .<Long, FileMetaInfo>chunk(stepFmiObsoleteDeleterBatchSize, transactionManager)
                .allowStartIfComplete(true)
                .reader(dbFMIObsoleteDeleterReader)
                .processor(processor)
                .writer(fileItemWriter)
                .build();
    }

    @Bean("dbJobStepUpdaterStep")
    public Step dbJobStepUpdaterStep(JobRepository jobRepository, TaskExecutor threadPoolTaskExecutor, JdbcPagingItemReader<Long> dbStepUpdaterReader, DbStepUpdaterProcessor processor, DbStepUpdaterWriter dbStepUpdaterWriter, HibernateTransactionManager transactionManager) {
        return new StepBuilder("dbJobStepUpdaterStep", jobRepository)
                .<Long, FileSystemItem>chunk(stepStepUpdaterBatchSize, transactionManager)
                .allowStartIfComplete(true)
                .taskExecutor(threadPoolTaskExecutor)
                .reader(dbStepUpdaterReader)
                .processor(processor)
                .writer(dbStepUpdaterWriter)
                .build();
    }

    @Bean("metaInfoBuilderStep")
    public Step metaInfoBuilderStep(JobRepository jobRepository, TaskExecutor threadPoolTaskExecutor, JdbcPagingItemReader<Long> metaInfoBuilderReader, MetaInfoProcessor processor, MetaInfoWriter metaInfoWriter, HibernateTransactionManager transactionManager) {
        return new StepBuilder("metaInfoBuilderStep", jobRepository)
                .<Long, FileSystemItem>chunk(stepStepUpdaterBatchSize, transactionManager)
                .allowStartIfComplete(true)
                .taskExecutor(threadPoolTaskExecutor)
                .reader(metaInfoBuilderReader)
                .processor(processor)
                .writer(metaInfoWriter)
                .build();
    }

    @Bean("externalMetaInfoBuilderStep")
    public Step externalMetaInfoBuilderStep(JobRepository jobRepository, JdbcPagingItemReader<Long> metaInfoBuilderReader, ExternalMetaInfoProcessor processor, ExternalMetaInfoWriter metaInfoWriter, HibernateTransactionManager transactionManager) {
        return new StepBuilder("externalMetaInfoBuilderStep", jobRepository)
                .<Long, FileSystemItem>chunk(stepStepUpdaterBatchSize, transactionManager)
                .allowStartIfComplete(true)
                .reader(metaInfoBuilderReader)
                .processor(processor)
                .writer(metaInfoWriter)
                .build();
    }

    @Bean("fileSystemItemHashStep")
    public Step fileSystemItemHashStep(JobRepository jobRepository, TaskExecutor threadPoolTaskExecutor, JdbcPagingItemReader<FileSystemItem> hashStorerReader, FileSystemItemHashProcessor processor, FileSystemItemHashWriter writer, HibernateTransactionManager transactionManager) {
        return new StepBuilder("fileSystemItemHashStep", jobRepository)
                .<FileSystemItem, FileSystemItem>chunk(stepStepUpdaterBatchSize, transactionManager)
                .allowStartIfComplete(true)
                .taskExecutor(threadPoolTaskExecutor)
                .reader(hashStorerReader)
                .processor(processor)
                .writer(writer)
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
        return jpaCursorItemReader;
    }

    @Bean("dbFMIObsoleteDeleterReader")
    public JdbcPagingItemReader<Long> dbFMIObsoleteDeleterReader() {
        JdbcPagingItemReader<Long> jdbcPagingItemReader = (new JdbcPagingItemReader<>());
        jdbcPagingItemReader.setDataSource(dataSource);
        jdbcPagingItemReader.setFetchSize(stepStepUpdaterBatchSize);
        jdbcPagingItemReader.setRowMapper((rs, rowNum) -> rs.getObject(1, Long.class));
        jdbcPagingItemReader.setQueryProvider(dbFMIObsoleteDeleterReaderPostgresQueryProvider());
        jdbcPagingItemReader.setSaveState(false);
        return jdbcPagingItemReader;
    }

    public PostgresPagingQueryProvider dbFMIObsoleteDeleterReaderPostgresQueryProvider() {
        PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
        queryProvider.setSelectClause("SELECT distinct fmi.id");
        queryProvider.setFromClause("FROM el_file_meta_info fmi, el_file_system_item fsi");
        queryProvider.setWhereClause("WHERE fmi.id not in (select distinct file_meta_info_id from el_file_system_item)");
        Map<String, Order> orderByKeys = new HashMap<>();
        orderByKeys.put("id", Order.ASCENDING);
        queryProvider.setSortKeys(orderByKeys);
        return queryProvider;
    }

    @Bean("dbStepUpdaterReader")
    public JdbcPagingItemReader<Long> dbStepUpdaterReader() {
        JdbcPagingItemReader<Long> jdbcPagingItemReader = (new JdbcPagingItemReader<>());
        jdbcPagingItemReader.setDataSource(dataSource);
        jdbcPagingItemReader.setFetchSize(stepStepUpdaterBatchSize);
        jdbcPagingItemReader.setRowMapper((rs, rowNum) -> rs.getObject(1, Long.class));
        jdbcPagingItemReader.setQueryProvider(getDbStepUpdaterPostgresQueryProvider());
        jdbcPagingItemReader.setSaveState(false);
        return jdbcPagingItemReader;
    }

    public PostgresPagingQueryProvider getDbStepUpdaterPostgresQueryProvider() {
        PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
        queryProvider.setSelectClause("SELECT id");
        queryProvider.setFromClause("FROM el_file_system_item");
        queryProvider.setWhereClause("WHERE record_status = 1");
        Map<String, Order> orderByKeys = new HashMap<>();
        orderByKeys.put("id", Order.ASCENDING);
        queryProvider.setSortKeys(orderByKeys);
        return queryProvider;
    }


    @Bean("hashStorerReader")
    public JdbcPagingItemReader<FileSystemItem> hashStorerReader() {
        JdbcPagingItemReader<FileSystemItem> jdbcPagingItemReader = (new JdbcPagingItemReader<>());
        jdbcPagingItemReader.setDataSource(dataSource);
        jdbcPagingItemReader.setFetchSize(stepStepUpdaterBatchSize);
        jdbcPagingItemReader.setRowMapper(new BeanPropertyRowMapper<>(FileSystemItem.class));
        jdbcPagingItemReader.setQueryProvider(getPostgresHashQueryProvider());
        jdbcPagingItemReader.setSaveState(false);
        return jdbcPagingItemReader;
    }

    private final DataSource dataSource;

    @Bean("metaInfoBuilderReader")
    public JdbcPagingItemReader<Long> metaInfoBuilderReader() {
        JdbcPagingItemReader<Long> jdbcPagingItemReader = (new JdbcPagingItemReader<>());
        jdbcPagingItemReader.setDataSource(dataSource);
        jdbcPagingItemReader.setFetchSize(stepStepUpdaterBatchSize);
        jdbcPagingItemReader.setRowMapper((rs, rowNum) -> rs.getObject(1, Long.class));
        jdbcPagingItemReader.setQueryProvider(getPostgresQueryProvider());
        jdbcPagingItemReader.setSaveState(false);
        return jdbcPagingItemReader;
    }


    public PostgresPagingQueryProvider getPostgresQueryProvider() {
        PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
        queryProvider.setSelectClause("SELECT id");
        queryProvider.setFromClause("FROM el_file_system_item");
        queryProvider.setWhereClause("WHERE record_status = 1");
        Map<String, Order> orderByKeys = new HashMap<>();
        orderByKeys.put("id", Order.ASCENDING);
        queryProvider.setSortKeys(orderByKeys);
        return queryProvider;
    }


    public PostgresPagingQueryProvider getPostgresHashQueryProvider() {
        PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
        queryProvider.setSelectClause("SELECT *");
        queryProvider.setFromClause("FROM el_file_system_item");
        queryProvider.setWhereClause("WHERE record_status = 1");
        Map<String, Order> orderByKeys = new HashMap<>();
        orderByKeys.put("id", Order.ASCENDING);
        queryProvider.setSortKeys(orderByKeys);
        return queryProvider;
    }

    @Bean("threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        int processors = Runtime.getRuntime().availableProcessors();
        log.info("\n\n\n====================\nYou have {} processors\n=====================\n\n\n", processors);
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(processors);
        taskExecutor.setCorePoolSize(processors);
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }

}
