package com.andreidodu.europealibrary.batch.indexer.config.step;

import com.andreidodu.europealibrary.batch.indexer.step.dbfsiobsoletedeleter.DbFSIObsoleteDeleterProcessor;
import com.andreidodu.europealibrary.batch.indexer.step.dbfsiobsoletedeleter.DbFSIObsoleteDeleterWriter;
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
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class DbFsiObsoleteDeleterStepConfig {
    private final DataSource dataSource;
    private final JobRepository jobRepository;
    private final DbFSIObsoleteDeleterProcessor processor;
    private final DbFSIObsoleteDeleterWriter fileItemWriter;
    private final HibernateTransactionManager transactionManager;
    @Value("${com.andreidodu.europea-library.job.indexer.step-fsi-obsolete-deleter.batch-size}")
    private Integer stepFsiObsoleteDeleterBatchSize;
    @Value("${com.andreidodu.europea-library.job.indexer.e-books-directory}")
    private String ebookDirectory;
    @Autowired
    @Qualifier("threadPoolTaskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Bean("dbFSIObsoleteDeleterStep")
    public Step dbFSIObsoleteDeleterStep(JdbcPagingItemReader<Long> dbFSIObsoleteDeleterReader) {
        return new StepBuilder("dbFSIObsoleteDeleterStep", jobRepository)
                .<Long, Long>chunk(stepFsiObsoleteDeleterBatchSize, transactionManager)
                .allowStartIfComplete(true)
                .taskExecutor(threadPoolTaskExecutor)
                .reader(dbFSIObsoleteDeleterReader)
                .processor(processor)
                .writer(fileItemWriter)
                .build();
    }

    @Bean("dbFSIObsoleteDeleterReader")
    public JdbcPagingItemReader<Long> dbFSIObsoleteDeleterReader() {
        JdbcPagingItemReader<Long> jdbcPagingItemReader = (new JdbcPagingItemReader<>());
        jdbcPagingItemReader.setDataSource(dataSource);
        jdbcPagingItemReader.setFetchSize(stepFsiObsoleteDeleterBatchSize);
        jdbcPagingItemReader.setRowMapper((rs, rowNum) -> rs.getObject(1, Long.class));
        jdbcPagingItemReader.setQueryProvider(dbFSIObsoleteDeleterReaderQueryProvider());
        jdbcPagingItemReader.setSaveState(false);
        return jdbcPagingItemReader;
    }

    public PostgresPagingQueryProvider dbFSIObsoleteDeleterReaderQueryProvider() {
        PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
        queryProvider.setSelectClause("SELECT fsi.id");
        queryProvider.setFromClause("FROM el_file_system_item fsi");
        queryProvider.setWhereClause("WHERE fsi.record_status = 2 OR (fsi.base_path NOT LIKE '" + new File(ebookDirectory).getParent() + "%')");
        Map<String, Order> orderByKeys = new HashMap<>();
        orderByKeys.put("id", Order.ASCENDING);
        queryProvider.setSortKeys(orderByKeys);
        return queryProvider;
    }
}
