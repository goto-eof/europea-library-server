package com.andreidodu.europealibrary.batch.indexer.config.step;

import com.andreidodu.europealibrary.batch.indexer.step.tagdeleter.DbTagObsoleteDeleterProcessor;
import com.andreidodu.europealibrary.batch.indexer.step.tagdeleter.DbTagObsoleteDeleterWriter;
import com.andreidodu.europealibrary.model.Tag;
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
import org.springframework.core.task.TaskExecutor;
import org.springframework.orm.hibernate5.HibernateTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DbTagObsoleteDeleterStepConfig {
    @Value("${com.andreidodu.europea-library.job.indexer.step-tag-obsolete-deleter.batch-size}")
    private Integer batchSize;

    private final DataSource dataSource;


    private final JobRepository jobRepository;
    private final DbTagObsoleteDeleterProcessor processor;
    private final DbTagObsoleteDeleterWriter fileItemWriter;
    private final HibernateTransactionManager transactionManager;
    private final TaskExecutor threadPoolTaskExecutor;

    @Bean("dbTagObsoleteDeleterStep")
    public Step dbTagObsoleteDeleterStep(JdbcPagingItemReader<Long> dbTagObsoleteDeleterReader) {
        return new StepBuilder("dbTagObsoleteDeleterStep", jobRepository)
                .<Long, Tag>chunk(batchSize, transactionManager)
                .allowStartIfComplete(true)
                .taskExecutor(threadPoolTaskExecutor)
                .reader(dbTagObsoleteDeleterReader)
                .processor(processor)
                .writer(fileItemWriter)
                .build();
    }

    @Bean("dbTagObsoleteDeleterReader")
    public JdbcPagingItemReader<Long> dbTagObsoleteDeleterReader() {
        JdbcPagingItemReader<Long> jdbcPagingItemReader = (new JdbcPagingItemReader<>());
        jdbcPagingItemReader.setDataSource(dataSource);
        jdbcPagingItemReader.setFetchSize(batchSize);
        jdbcPagingItemReader.setRowMapper((rs, rowNum) -> rs.getObject(1, Long.class));
        jdbcPagingItemReader.setQueryProvider(dbTagObsoleteDeleterReaderPostgresQueryProvider());
        jdbcPagingItemReader.setSaveState(false);
        return jdbcPagingItemReader;
    }

    public PostgresPagingQueryProvider dbTagObsoleteDeleterReaderPostgresQueryProvider() {
        PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
        queryProvider.setSelectClause("SELECT distinct t.id");
        queryProvider.setFromClause("FROM el_tag t");
        queryProvider.setWhereClause("where t.id not in (select distinct fmit.tag_id from el_file_meta_info_tag fmit)");
        Map<String, Order> orderByKeys = new HashMap<>();
        orderByKeys.put("id", Order.ASCENDING);
        queryProvider.setSortKeys(orderByKeys);
        return queryProvider;
    }
}
