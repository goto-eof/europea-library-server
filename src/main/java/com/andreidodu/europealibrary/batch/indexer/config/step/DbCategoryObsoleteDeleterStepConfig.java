package com.andreidodu.europealibrary.batch.indexer.config.step;

import com.andreidodu.europealibrary.batch.indexer.step.categorydeleter.DbCategoryObsoleteDeleterProcessor;
import com.andreidodu.europealibrary.batch.indexer.step.categorydeleter.DbCategoryObsoleteDeleterWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DbCategoryObsoleteDeleterStepConfig {
    private final DataSource dataSource;
    private final JobRepository jobRepository;
    private final DbCategoryObsoleteDeleterProcessor processor;
    private final DbCategoryObsoleteDeleterWriter fileItemWriter;
    private final HibernateTransactionManager transactionManager;
    @Value("${com.andreidodu.europea-library.job.indexer.step-category-obsolete-deleter.batch-size}")
    private Integer batchSize;
    @Autowired
    @Qualifier("threadPoolTaskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Bean("dbCategoryObsoleteDeleterStep")
    public Step dbCategoryObsoleteDeleterStep(JdbcPagingItemReader<Long> dbCategoryObsoleteDeleterReader) {
        return new StepBuilder("dbCategoryObsoleteDeleterStep", jobRepository)
                .<Long, Long>chunk(batchSize, transactionManager)
                .allowStartIfComplete(true)
                .taskExecutor(threadPoolTaskExecutor)
                .reader(dbCategoryObsoleteDeleterReader)
                .processor(processor)
                .writer(fileItemWriter)
                .build();
    }

    @Bean("dbCategoryObsoleteDeleterReader")
    public JdbcPagingItemReader<Long> dbCategoryObsoleteDeleterReader() {
        JdbcPagingItemReader<Long> jdbcPagingItemReader = (new JdbcPagingItemReader<>());
        jdbcPagingItemReader.setDataSource(dataSource);
        jdbcPagingItemReader.setFetchSize(batchSize);
        jdbcPagingItemReader.setRowMapper((rs, rowNum) -> rs.getObject(1, Long.class));
        jdbcPagingItemReader.setQueryProvider(dbCategoryObsoleteDeleterReaderPostgresQueryProvider());
        jdbcPagingItemReader.setSaveState(false);
        return jdbcPagingItemReader;
    }

    public PostgresPagingQueryProvider dbCategoryObsoleteDeleterReaderPostgresQueryProvider() {
        PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
        queryProvider.setSelectClause("SELECT distinct c.id");
        queryProvider.setFromClause("FROM el_category c");
        queryProvider.setWhereClause("where c.id not in (select distinct bic.category_id from el_book_info_category bic)");
        Map<String, Order> orderByKeys = new HashMap<>();
        orderByKeys.put("id", Order.ASCENDING);
        queryProvider.setSortKeys(orderByKeys);
        return queryProvider;
    }
}
