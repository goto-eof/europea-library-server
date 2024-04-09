package com.andreidodu.europealibrary.batch.indexer.config.step;

import com.andreidodu.europealibrary.batch.indexer.step.parentassociator.ParentAssociatorProcessor;
import com.andreidodu.europealibrary.batch.indexer.step.parentassociator.ParentAssociatorWriter;
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
public class ParentAssociatorStepConfig {
    @Value("${com.andreidodu.europea-library.job.indexer.step-parent-associator-step.batch-size}")
    private Integer batchSize;

    private final JobRepository jobRepository;
    private final ParentAssociatorProcessor processor;
    private final ParentAssociatorWriter parentAssociatorWriter;
    private final HibernateTransactionManager transactionManager;
    @Autowired
    @Qualifier("threadPoolTaskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private final DataSource dataSource;

    @Bean("parentAssociatorStep")
    public Step parentAssociatorStep(JdbcPagingItemReader<Long> parentAssociatorReader) {
        return new StepBuilder("parentAssociatorStep", jobRepository)
                .<Long, FileSystemItem>chunk(batchSize, transactionManager)
                .allowStartIfComplete(true)
                .taskExecutor(threadPoolTaskExecutor)
                .reader(parentAssociatorReader)
                .processor(processor)
                .writer(parentAssociatorWriter)
                .build();
    }

    @Bean("parentAssociatorReader")
    public JdbcPagingItemReader<Long> parentAssociatorReader() {
        JdbcPagingItemReader<Long> jdbcPagingItemReader = (new JdbcPagingItemReader<>());
        jdbcPagingItemReader.setDataSource(dataSource);
        jdbcPagingItemReader.setFetchSize(batchSize);
        jdbcPagingItemReader.setRowMapper((rs, rowNum) -> rs.getObject(1, Long.class));
        jdbcPagingItemReader.setQueryProvider(parentAssociatorReaderQueryProvider());
        jdbcPagingItemReader.setSaveState(false);
        return jdbcPagingItemReader;
    }

    public PostgresPagingQueryProvider parentAssociatorReaderQueryProvider() {
        PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
        queryProvider.setSelectClause("SELECT distinct fsi.id");
        queryProvider.setFromClause("FROM el_file_system_item fsi");
        queryProvider.setWhereClause("WHERE fsi.record_status = 1");
        Map<String, Order> orderByKeys = new HashMap<>();
        orderByKeys.put("id", Order.ASCENDING);
        queryProvider.setSortKeys(orderByKeys);
        return queryProvider;
    }
}
