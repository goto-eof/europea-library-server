package com.andreidodu.europealibrary.batch.indexer.config.step;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@Configuration
public class CommonStepConfig {
    @Bean("threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        int processors = Runtime.getRuntime().availableProcessors() - 1;
        log.debug("\n\n\n====================\nYou have {} processors available for processing\n=====================\n\n\n", processors);
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(processors);
        taskExecutor.setCorePoolSize(processors);
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }
}
