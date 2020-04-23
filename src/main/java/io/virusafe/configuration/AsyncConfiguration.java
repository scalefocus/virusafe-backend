package io.virusafe.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfiguration {

    /**
     * Create asyncThreadPoolTaskExecutor bean, initialized based on the pool settings provided by
     * autowired AsyncTaskExecutorProperties.
     *
     * @param asyncTaskExecutorProperties autowired properties configuration
     * @return the initialized asyncThreadPoolTaskExecutor bean
     */
    @Bean(name = "asyncThreadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor(final AsyncTaskExecutorProperties asyncTaskExecutorProperties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(asyncTaskExecutorProperties.getCorePoolSize());
        executor.setQueueCapacity(asyncTaskExecutorProperties.getQueueCapacity());
        executor.setMaxPoolSize(asyncTaskExecutorProperties.getMaxPoolSize());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadNamePrefix("Async-");
        executor.initialize();
        return executor;
    }

    @Data
    @Configuration
    @ConfigurationProperties(prefix = "async.task.executor")
    public static class AsyncTaskExecutorProperties {
        private Integer corePoolSize;
        private Integer queueCapacity;
        private Integer maxPoolSize;
    }
}