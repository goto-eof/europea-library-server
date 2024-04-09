package com.andreidodu.europealibrary.batch.indexer.config.step;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@Configuration
public class CommonStepConfig {

    @Value("${com.andreidodu.europea-library.core-pool-size}")
    private Integer corePoolSize;
    @Value("${com.andreidodu.europea-library.max-pool-size}")
    private Integer maxPoolSize;

    @Primary
    @Bean(name = "threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        int corePoolSize = Runtime.getRuntime().availableProcessors() - 1;
        int maxPoolSize = corePoolSize * 2;

        if (isPoolSizeValidValue(this.corePoolSize)) {
            corePoolSize = this.corePoolSize;
        }
        if (isPoolSizeValidValue(this.maxPoolSize)) {
            maxPoolSize = this.maxPoolSize;
        }

        log.debug("\n====================\nI will use {} as corePoolSize and {} as maxPoolSize for processing\n=====================\n", corePoolSize, maxPoolSize);
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(corePoolSize);
        taskExecutor.setMaxPoolSize(maxPoolSize);
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }

    private boolean isPoolSizeValidValue(Integer poolSize) {
        return poolSize != null && poolSize > 0;
    }
}
