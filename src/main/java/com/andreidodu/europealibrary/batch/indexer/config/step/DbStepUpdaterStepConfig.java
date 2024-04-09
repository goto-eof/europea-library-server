package com.andreidodu.europealibrary.batch.indexer.config.step;

import com.andreidodu.europealibrary.batch.indexer.step.dbstepupdater.DbStepUpdaterProcessor;
import com.andreidodu.europealibrary.batch.indexer.step.dbstepupdater.DbStepUpdaterWriter;
import com.andreidodu.europealibrary.model.FileSystemItem;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class DbStepUpdaterStepConfig {
    @Value("${com.andreidodu.europea-library.job.indexer.step-step-updater.batch-size}")
    private Integer batchSize;

    private final DataSource dataSource;
    private final JobRepository jobRepository;
    @Autowired
    @Qualifier("threadPoolTaskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private final DbStepUpdaterProcessor processor;
    private final DbStepUpdaterWriter dbStepUpdaterWriter;
    private final HibernateTransactionManager transactionManager;

    @Bean("dbJobStepUpdaterStep")
    public Step dbJobStepUpdaterStep( JdbcPagingItemReader<Long> dbStepUpdaterReader) {
        return new StepBuilder("dbJobStepUpdaterStep", jobRepository)
                .<Long, FileSystemItem>chunk(batchSize, transactionManager)
                .allowStartIfComplete(true)
                .taskExecutor(threadPoolTaskExecutor)
                .reader(dbStepUpdaterReader)
                .processor(processor)
                .writer(dbStepUpdaterWriter)
                .build();
    }


    @Bean("dbStepUpdaterReader")
    public JdbcPagingItemReader<Long> dbStepUpdaterReader() {
        JdbcPagingItemReader<Long> jdbcPagingItemReader = (new JdbcPagingItemReader<>());
        jdbcPagingItemReader.setDataSource(dataSource);
        jdbcPagingItemReader.setFetchSize(batchSize);
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
}
