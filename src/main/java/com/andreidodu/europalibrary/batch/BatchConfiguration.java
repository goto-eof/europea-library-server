package com.andreidodu.europalibrary.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.io.File;
import java.util.Objects;

@Configuration
@EnableJpaRepositories
@RequiredArgsConstructor
public class BatchConfiguration {
    private final BatchDbConfig batchDbConfig;

    private final FileItemReader fileItemReader;

    @Bean
    public Job indexerJob(JobRepository jobRepository, Step indexDirectoriesAndFiles) {
        return new JobBuilder("indexerJob", jobRepository)
                .start(indexDirectoriesAndFiles)
                .build();
    }

    @Bean
    public Step indexDirectoriesAndFiles(JobRepository jobRepository, FileItemProcessor processor, FileItemWriter writer, HibernateTransactionManager transactionManager) {
        return new StepBuilder("indexDirectoriesAndFiles", jobRepository)
                .<File, File>chunk(25, transactionManager)
                .reader(fileItemReader)
                .allowStartIfComplete(true)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public HibernateTransactionManager transactionManager() {
        return new HibernateTransactionManager(Objects.requireNonNull(sessionFactory().getObject()));
    }

    @Bean(name = "entityManagerFactory")
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        return sessionFactory;
    }


    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .url(batchDbConfig.getUrl())
                .username(batchDbConfig.getUsername())
                .password(batchDbConfig.getPassword())
                .build();
    }
}
