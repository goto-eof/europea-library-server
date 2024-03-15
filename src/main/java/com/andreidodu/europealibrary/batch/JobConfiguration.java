package com.andreidodu.europealibrary.batch;

import com.andreidodu.europealibrary.batch.step.dbdelete.DbDeleteFileItemProcessor;
import com.andreidodu.europealibrary.batch.step.dbdelete.DbDeleteFileItemWriter;
import com.andreidodu.europealibrary.batch.step.dbupdate.DbFileItemProcessor;
import com.andreidodu.europealibrary.batch.step.dbupdate.DbFileItemWriter;
import com.andreidodu.europealibrary.batch.step.file.FileItemProcessor;
import com.andreidodu.europealibrary.batch.step.file.FileItemReader;
import com.andreidodu.europealibrary.batch.step.file.FileItemWriter;
import com.andreidodu.europealibrary.dto.FileDTO;
import com.andreidodu.europealibrary.model.FileSystemItem;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;

import javax.sql.DataSource;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class JobConfiguration {
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private Integer batchSize;

    final private EntityManagerFactory emFactory;

    @Bean("jobIndexer")
    public Job indexerJob(JobRepository jobRepository, Step indexDirectoriesAndFilesStep, Step deleteDbFilesStep, Step finalizeJobStep) {
        return new JobBuilder("indexerJob", jobRepository)
                .incrementer(new SampleIncrementer())
                .start(indexDirectoriesAndFilesStep)
                .next(deleteDbFilesStep)
                .next(finalizeJobStep)
                .build();
    }

    @Bean
    public Step indexDirectoriesAndFilesStep(JobRepository jobRepository, FileItemProcessor processor, FileItemReader fileItemReader, FileItemWriter writer, HibernateTransactionManager transactionManager) {
        return new StepBuilder("indexDirectoriesAndFiles", jobRepository)
                .<File, FileDTO>chunk(batchSize, transactionManager)
                .reader(fileItemReader)
                .allowStartIfComplete(true)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Step deleteDbFilesStep(JpaCursorItemReader<FileSystemItem> dbDeleteFileItemReader, JobRepository jobRepository, DbDeleteFileItemProcessor processor, DbDeleteFileItemWriter fileItemWriter, HibernateTransactionManager transactionManager) {
        return new StepBuilder("deleteDbFilesStep", jobRepository)
                .<FileSystemItem, FileSystemItem>chunk(batchSize, transactionManager)
                .allowStartIfComplete(true)
                .reader(dbDeleteFileItemReader)
                .processor(processor)
                .writer(fileItemWriter)
                .build();
    }

    @Bean
    public Step finalizeJobStep(JobRepository jobRepository, JpaCursorItemReader<FileSystemItem> dbFileItemReader, DbFileItemProcessor processor, DbFileItemWriter dbFileItemWriter, HibernateTransactionManager transactionManager) {
        return new StepBuilder("finalizeJobStep", jobRepository)
                .<FileSystemItem, FileSystemItem>chunk(batchSize, transactionManager)
                .allowStartIfComplete(true)
                .reader(dbFileItemReader)
                .processor(processor)
                .writer(dbFileItemWriter)
                .build();
    }

    @Bean
    public JpaCursorItemReader<FileSystemItem> dbDeleteFileItemReader() {
        JpaCursorItemReader<FileSystemItem> jpaCursorItemReader = (new JpaCursorItemReader<FileSystemItem>());
        jpaCursorItemReader.setEntityManagerFactory(emFactory);
        jpaCursorItemReader.setQueryString("SELECT p FROM FileSystemItem p where jobStep = :step");
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("step", JobStepEnum.READY.getStepNumber());
        jpaCursorItemReader.setParameterValues(parameterValues);
        jpaCursorItemReader.setSaveState(true);
        return jpaCursorItemReader;
    }

    @Bean
    public JpaCursorItemReader<FileSystemItem> dbFileItemReader() {
        JpaCursorItemReader<FileSystemItem> jpaCursorItemReader = (new JpaCursorItemReader<FileSystemItem>());
        jpaCursorItemReader.setEntityManagerFactory(emFactory);
        jpaCursorItemReader.setQueryString("SELECT p FROM FileSystemItem p where jobStep = :step");
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("step", JobStepEnum.INSERTED.getStepNumber());
        jpaCursorItemReader.setParameterValues(parameterValues);
        jpaCursorItemReader.setSaveState(true);
        return jpaCursorItemReader;
    }

}
