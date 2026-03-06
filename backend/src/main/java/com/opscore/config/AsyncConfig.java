package com.opscore.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Async configuration for Spring's @Async annotation.
 * 
 * Learning points:
 * - @EnableAsync activates Spring's async method execution capability
 * - ThreadPoolTaskExecutor manages thread pool for async operations
 * - Configure core pool size, max pool size, and queue capacity based on load
 * - Custom exception handler catches errors in async methods
 * - Thread naming helps with debugging
 * 
 * Use @Async on methods to run them in a separate thread
 */
@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig implements AsyncConfigurer {
    
    /**
     * Configure the executor for @Async methods
     * 
     * Pool sizing guidelines:
     * - Core pool size: Number of threads to keep alive
     * - Max pool size: Maximum threads for burst traffic
     * - Queue capacity: Tasks waiting when all threads busy
     */
    @Override
    @Bean(name = "taskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // Core threads always alive
        executor.setCorePoolSize(5);
        
        // Max threads during peak load
        executor.setMaxPoolSize(10);
        
        // Queue for pending tasks
        executor.setQueueCapacity(100);
        
        // Thread naming for debugging
        executor.setThreadNamePrefix("async-");
        
        // Wait for tasks to complete on shutdown
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        
        executor.initialize();
        log.info("Async executor configured with core={}, max={}, queue={}", 
                5, 10, 100);
        
        return executor;
    }
    
    /**
     * Handle uncaught exceptions in async methods
     * Without this, exceptions in @Async methods are silently swallowed
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, params) -> {
            log.error("Async execution failed in method: {}", method.getName(), throwable);
            log.error("Parameters: {}", params);
        };
    }
}
