package com.andreidodu.europealibrary.batch.indexer.config.step;

import com.andreidodu.europealibrary.batch.indexer.step.metainfotagassociation.MetaInfoTagAssociationBulkWriter;
import com.andreidodu.europealibrary.batch.indexer.step.metainfotagassociation.MetaInfoTagAssociationProcessor;
import com.andreidodu.europealibrary.batch.indexer.step.metainfotagassociation.MetaInfoTagAssociationStepListener;
import com.mysema.commons.lang.Pair;
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
public class MetaInfoTagAssociatorStepConfig {
    private final DataSource dataSource;
    private final JobRepository jobRepository;
    private final MetaInfoTagAssociationProcessor processor;
    private final MetaInfoTagAssociationBulkWriter writer;
    private final HibernateTransactionManager transactionManager;
    private final MetaInfoTagAssociationStepListener metaInfoTagAssociationStepListener;
    @Value("${com.andreidodu.europea-library.job.indexer.meta-info-tag-associator.batch-size}")
    private Integer batchSize;
    @Autowired
    @Qualifier("threadPoolTaskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Bean("metaInfoTagAssociatorStep")
    public Step metaInfoTagAssociatorStep(JdbcPagingItemReader<Pair<Long, Long>> metaInfoTagAssociatorReader) {
        return new StepBuilder("metaInfoTagAssociatorStep", jobRepository)
                .<Pair<Long, Long>, Pair<Long, Long>>chunk(batchSize, transactionManager)
                .allowStartIfComplete(true)
                .taskExecutor(threadPoolTaskExecutor)
                .reader(metaInfoTagAssociatorReader)
                .processor(processor)
                .writer(writer)
                .listener(metaInfoTagAssociationStepListener)
                .build();
    }


    @Bean("metaInfoTagAssociatorReader")
    public JdbcPagingItemReader<Pair<Long, Long>> metaInfoTagAssociatorReader() {
        JdbcPagingItemReader<Pair<Long, Long>> jdbcPagingItemReader = (new JdbcPagingItemReader<>());
        jdbcPagingItemReader.setDataSource(dataSource);
        jdbcPagingItemReader.setFetchSize(batchSize);
        jdbcPagingItemReader.setRowMapper((rs, rowNum) -> new Pair<Long, Long>(rs.getLong(1), rs.getLong(2)));
        jdbcPagingItemReader.setQueryProvider(getMetaInfoTagAssociatorPostgresQueryProvider());
        jdbcPagingItemReader.setSaveState(false);
        return jdbcPagingItemReader;
    }

    public PostgresPagingQueryProvider getMetaInfoTagAssociatorPostgresQueryProvider() {
        PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
        queryProvider.setSelectClause("select ta.target_id, t.id");
        queryProvider.setFromClause("from el_tmp_association ta, el_tag t");
        queryProvider.setWhereClause("where t.name = ta.value and (ta.target_id, t.id) not in (select mt.file_meta_info_id, mt.tag_id from el_file_meta_info_tag mt)");
        Map<String, Order> orderByKeys = new HashMap<>();
        orderByKeys.put("target_id", Order.ASCENDING);
        queryProvider.setSortKeys(orderByKeys);
        return queryProvider;
    }
}
