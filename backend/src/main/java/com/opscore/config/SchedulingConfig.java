package com.opscore.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * Spring Scheduling configuration.
 * 
 * Learning points:
 * - @EnableScheduling activates Spring's scheduling capabilities
 * - @Scheduled annotation marks methods to run on schedule
 * - Cron expressions for complex schedules
 * - Thread pool for scheduled tasks
 * - Task isolation and error handling
 * 
 * Cron expression format:
 * second minute hour day-of-month month day-of-week
 * Example: "0 0 2 * * ?" = 2 AM daily
 * 
 * Fixed delay vs fixed rate:
 * - fixedDelay: Wait X ms AFTER previous execution completes
 * - fixedRate: Run every X ms regardless of previous execution
 */
@Configuration
@EnableScheduling
@Slf4j
public class SchedulingConfig implements SchedulingConfigurer {
    
    /**
     * Configure thread pool for scheduled tasks.
     * Separate from async thread pool for better control.
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        
        // Core pool size for scheduled tasks
        scheduler.setPoolSize(5);
        
        // Thread naming for debugging
        scheduler.setThreadNamePrefix("scheduled-");
        
        // Allow tasks to complete on shutdown
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(60);
        
        scheduler.initialize();
        taskRegistrar.setTaskScheduler(scheduler);
        
        log.info("Scheduled task executor configured with pool size: 5");
    }
}
