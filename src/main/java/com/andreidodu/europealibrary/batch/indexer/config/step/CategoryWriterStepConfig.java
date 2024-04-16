package com.andreidodu.europealibrary.batch.indexer.config.step;

import com.andreidodu.europealibrary.batch.indexer.step.categorywriter.CategoryWriterBulkWriter;
import com.andreidodu.europealibrary.batch.indexer.step.categorywriter.CategoryWriterProcessor;
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
public class CategoryWriterStepConfig {
    private final DataSource dataSource;
    private final JobRepository jobRepository;
    private final CategoryWriterProcessor processor;
    private final CategoryWriterBulkWriter writer;
    private final HibernateTransactionManager transactionManager;
    @Value("${com.andreidodu.europea-library.job.indexer.step-category-writer.batch-size}")
    private Integer batchSize;
    @Autowired
    @Qualifier("threadPoolTaskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Bean("categoryWriterStep")
    public Step categoryWriterStep(JdbcPagingItemReader<String> categoryWriterReader) {
        return new StepBuilder("categoryWriterStep", jobRepository)
                .<String, String>chunk(batchSize, transactionManager)
                .allowStartIfComplete(true)
                .taskExecutor(threadPoolTaskExecutor)
                .reader(categoryWriterReader)
                .processor(processor)
                .writer(writer)
                .build();
    }


    @Bean("categoryWriterReader")
    public JdbcPagingItemReader<String> categoryWriterReader() {
        JdbcPagingItemReader<String> jdbcPagingItemReader = (new JdbcPagingItemReader<>());
        jdbcPagingItemReader.setDataSource(dataSource);
        jdbcPagingItemReader.setFetchSize(batchSize);
        jdbcPagingItemReader.setRowMapper((rs, rowNum) -> rs.getObject(1, String.class));
        jdbcPagingItemReader.setQueryProvider(getCategoryWriterPostgresQueryProvider());
        jdbcPagingItemReader.setSaveState(false);
        return jdbcPagingItemReader;
    }

    public PostgresPagingQueryProvider getCategoryWriterPostgresQueryProvider() {
        PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
        queryProvider.setSelectClause("SELECT distinct eta.value");
        queryProvider.setFromClause("FROM el_tmp_association eta");
        queryProvider.setWhereClause("WHERE eta.value not in (select t.name from el_category t)");
        Map<String, Order> orderByKeys = new HashMap<>();
        orderByKeys.put("value", Order.ASCENDING);
        queryProvider.setSortKeys(orderByKeys);
        return queryProvider;
    }
}
