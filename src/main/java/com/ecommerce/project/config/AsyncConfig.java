package com.ecommerce.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig {
    // Enables @Async annotation for asynchronous email sending
    // Enables @Scheduled annotation for scheduled tasks (reminder processing)

    /**
     * Dedicated thread pool executor for email operations.
     * This ensures email sending does not block the main application threads
     * and provides controlled concurrency for email operations.
     * 
     * Configuration:
     * - Core pool size: 2 (minimum threads always available)
     * - Max pool size: 5 (maximum threads under load)
     * - Queue capacity: 100 (pending tasks before rejection)
     * - Thread name prefix: "email-" (for easy identification in logs)
     * 
     * @return configured Executor for email operations
     */
    @Bean(name = "emailExecutor")
    public Executor emailExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("email-");
        executor.initialize();
        return executor;
    }
}


