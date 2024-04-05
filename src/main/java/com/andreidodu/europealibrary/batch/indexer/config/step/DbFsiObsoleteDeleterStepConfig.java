package com.andreidodu.europealibrary.batch.indexer.config.step;

import com.andreidodu.europealibrary.batch.indexer.step.dbfsiobsoletedeleter.DbFSIObsoleteDeleterProcessor;
import com.andreidodu.europealibrary.batch.indexer.step.dbfsiobsoletedeleter.DbFSIObsoleteDeleterWriter;
import com.andreidodu.europealibrary.model.FileSystemItem;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.orm.hibernate5.HibernateTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class DbFsiObsoleteDeleterStepConfig {
    @Value("${com.andreidodu.europea-library.job.indexer.step-fsi-obsolete-deleter.batch-size}")
    private Integer stepFsiObsoleteDeleterBatchSize;
    @Value("${com.andreidodu.europea-library.job.indexer.e-books-directory}")
    private String ebookDirectory;

    private final DataSource dataSource;

    @Bean("dbFSIObsoleteDeleterStep")
    public Step dbFSIObsoleteDeleterStep(TaskExecutor threadPoolTaskExecutor, JdbcPagingItemReader<FileSystemItem> dbFSIObsoleteDeleterReader, JobRepository jobRepository, DbFSIObsoleteDeleterProcessor processor, DbFSIObsoleteDeleterWriter fileItemWriter, HibernateTransactionManager transactionManager) {
        return new StepBuilder("dbFSIObsoleteDeleterStep", jobRepository)
                .<FileSystemItem, FileSystemItem>chunk(stepFsiObsoleteDeleterBatchSize, transactionManager)
                .allowStartIfComplete(true)
                .taskExecutor(threadPoolTaskExecutor)
                .reader(dbFSIObsoleteDeleterReader)
                .processor(processor)
                .writer(fileItemWriter)
                .build();
    }

    @Bean("dbFSIObsoleteDeleterReader")
    public JdbcPagingItemReader<FileSystemItem> dbFSIObsoleteDeleterReader() {
        JdbcPagingItemReader<FileSystemItem> jdbcPagingItemReader = (new JdbcPagingItemReader<>());
        jdbcPagingItemReader.setDataSource(dataSource);
        jdbcPagingItemReader.setFetchSize(stepFsiObsoleteDeleterBatchSize);
        jdbcPagingItemReader.setRowMapper(new BeanPropertyRowMapper<>(FileSystemItem.class));
        jdbcPagingItemReader.setQueryProvider(dbFSIObsoleteDeleterReaderQueryProvider());
        jdbcPagingItemReader.setSaveState(false);
        return jdbcPagingItemReader;
    }

    public PostgresPagingQueryProvider dbFSIObsoleteDeleterReaderQueryProvider() {
        PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
        queryProvider.setSelectClause("SELECT *");
        queryProvider.setFromClause("FROM el_file_system_item fsi");
        queryProvider.setWhereClause("WHERE fsi.record_status = 2 OR (fsi.base_path NOT LIKE '" + ebookDirectory + "%')");
        Map<String, Order> orderByKeys = new HashMap<>();
        orderByKeys.put("id", Order.ASCENDING);
        queryProvider.setSortKeys(orderByKeys);
        return queryProvider;
    }
}
