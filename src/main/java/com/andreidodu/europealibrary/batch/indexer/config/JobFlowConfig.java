package com.andreidodu.europealibrary.batch.indexer.config;

import com.andreidodu.europealibrary.batch.indexer.constants.JobConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JobFlowConfig {

    private final JobRepository jobRepository;
    private final Step dbTagObsoleteDeleterStep;
    private final Step dbCategoryObsoleteDeleterStep;
    private final Step fileSystemItemHashStep;
    private final Step metaInfoBuilderStep;
    private final Step externalMetaInfoBuilderStep;
    private final Step fileIndexerAndCataloguerStep;
    private final Step dbFSIObsoleteDeleterStep;
    private final Step dbFMIObsoleteDeleterStep;
    private final Step dbJobStepUpdaterStep;
    private final Step finalizationStep;
    private final Step initializationStep;
    private final Step parentAssociatorStep;
    private final Step tagWriterStep;
    private final Step metaInfoTagAssociatorStep;
    private final Step categoryWriterStep;
    private final Step bookInfoCategoryAssociatorStep;
    private final JobExecutionListener indexerJobExecutionListener;

    @Bean("indexerJob")
    public Job indexerJob() {
        return new JobBuilder(JobConst.JOB_INDEXER_NAME, jobRepository)
                .listener(indexerJobExecutionListener)
                .start(initializationStep)
                .on(ExitStatus.COMPLETED.getExitCode()).to(fileIndexerAndCataloguerStep)
                .on(ExitStatus.COMPLETED.getExitCode()).to(fileSystemItemHashStep)
                .on(ExitStatus.COMPLETED.getExitCode()).to(parentAssociatorStep)
                .on(ExitStatus.COMPLETED.getExitCode()).to(metaInfoBuilderStep)
                .on(ExitStatus.COMPLETED.getExitCode()).to(tagWriterStep)
                .on(ExitStatus.COMPLETED.getExitCode()).to(metaInfoTagAssociatorStep)
                .on(ExitStatus.COMPLETED.getExitCode()).to(externalMetaInfoBuilderStep)
                .from(externalMetaInfoBuilderStep).on(ExitStatus.FAILED.getExitCode()).to(categoryWriterStep)
                .from(externalMetaInfoBuilderStep).on(ExitStatus.COMPLETED.getExitCode()).to(categoryWriterStep)
                .on(ExitStatus.COMPLETED.getExitCode()).to(bookInfoCategoryAssociatorStep)
                .on(ExitStatus.COMPLETED.getExitCode()).to(dbFSIObsoleteDeleterStep)
                .on(ExitStatus.COMPLETED.getExitCode()).to(dbFMIObsoleteDeleterStep)
                .on(ExitStatus.COMPLETED.getExitCode()).to(dbTagObsoleteDeleterStep)
                .on(ExitStatus.COMPLETED.getExitCode()).to(dbCategoryObsoleteDeleterStep)
                .on(ExitStatus.COMPLETED.getExitCode()).to(dbJobStepUpdaterStep)
                .on(ExitStatus.COMPLETED.getExitCode()).to(finalizationStep)
                .end()
                .build();
    }

}
