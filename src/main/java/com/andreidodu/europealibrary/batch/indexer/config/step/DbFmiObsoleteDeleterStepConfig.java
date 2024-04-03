package com.andreidodu.europealibrary.batch.indexer.config.step;

import com.andreidodu.europealibrary.batch.indexer.step.dbfmiobsoletedeleter.DbFMIObsoleteDeleterProcessor;
import com.andreidodu.europealibrary.batch.indexer.step.dbfmiobsoletedeleter.DbFMIObsoleteDeleterWriter;
import com.andreidodu.europealibrary.model.FileMetaInfo;
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
import org.springframework.orm.hibernate5.HibernateTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class DbFmiObsoleteDeleterStepConfig {
    @Value("${com.andreidodu.europea-library.job.indexer.step-fsi-obsolete-deleter.batch-size}")
    private Integer stepFsiObsoleteDeleterBatchSize;
    @Value("${com.andreidodu.europea-library.job.indexer.step-step-updater.batch-size}")
    private Integer batchSize;
    @Value("${com.andreidodu.europea-library.job.indexer.step-fmi-obsolete-deleter.batch-size}")
    private Integer stepFmiObsoleteDeleterBatchSize;

    private final DataSource dataSource;

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

    @Bean("dbFMIObsoleteDeleterReader")
    public JdbcPagingItemReader<Long> dbFMIObsoleteDeleterReader() {
        JdbcPagingItemReader<Long> jdbcPagingItemReader = (new JdbcPagingItemReader<>());
        jdbcPagingItemReader.setDataSource(dataSource);
        jdbcPagingItemReader.setFetchSize(batchSize);
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
}
