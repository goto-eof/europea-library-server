package com.andreidodu.europealibrary.batch.indexer.config.step;

import com.andreidodu.europealibrary.batch.indexer.step.externalapi.ExternalMetaInfoProcessor;
import com.andreidodu.europealibrary.batch.indexer.step.metainfo.MetaInfoBulkWriter;
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
import org.springframework.orm.hibernate5.HibernateTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class ExternalMetaInfoStepConfig {
    private final JobRepository jobRepository;
    private final JdbcPagingItemReader<Long> metaInfoBuilderReader;
    private final ExternalMetaInfoProcessor processor;
    private final MetaInfoBulkWriter metaInfoWriter;
    private final HibernateTransactionManager transactionManager;
    private final ExternalMetaInfoStepListener externalMetaInfoStepListener;
    private final DataSource dataSource;
    @Value("${com.andreidodu.europea-library.job.indexer.step-ext-meta-info-writer.batch-size}")
    private Integer batchSize;

    @Bean("externalMetaInfoBuilderStep")
    public Step externalMetaInfoBuilderStep(JdbcPagingItemReader<Long> externalMetaInfoBuilderReader) {
        return new StepBuilder("externalMetaInfoBuilderStep", jobRepository)
                .<Long, FileSystemItem>chunk(batchSize, transactionManager)
                .allowStartIfComplete(true)
                .reader(externalMetaInfoBuilderReader)
                .processor(processor)
                .writer(metaInfoWriter)
                .listener(externalMetaInfoStepListener)
                .build();
    }


    @Bean("externalMetaInfoBuilderReader")
    public JdbcPagingItemReader<Long> externalMetaInfoBuilderReader() {
        JdbcPagingItemReader<Long> jdbcPagingItemReader = (new JdbcPagingItemReader<>());
        jdbcPagingItemReader.setDataSource(dataSource);
        jdbcPagingItemReader.setFetchSize(batchSize);
        jdbcPagingItemReader.setRowMapper((rs, rowNum) -> rs.getObject(1, Long.class));
        jdbcPagingItemReader.setQueryProvider(getPostgresQueryProvider());
        jdbcPagingItemReader.setSaveState(false);
        return jdbcPagingItemReader;
    }

    public PostgresPagingQueryProvider getPostgresQueryProvider() {
        PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
        queryProvider.setSelectClause("SELECT distinct fsi.id, base_path");
        queryProvider.setFromClause("FROM el_file_system_item fsi, el_file_meta_info fmi, el_book_info bi");
        queryProvider.setWhereClause("WHERE fsi.record_status = 1 and fsi.is_directory is false and fsi.file_meta_info_id = fmi.id and bi.file_meta_info_id=fmi.id and bi.web_retrievement_status is null and (bi.isbn13 is not null or bi.authors is not null or bi.publisher is not null)");
        Map<String, Order> orderByKeys = new HashMap<>();
        orderByKeys.put("base_path", Order.ASCENDING);
        queryProvider.setSortKeys(orderByKeys);
        return queryProvider;
    }
}
