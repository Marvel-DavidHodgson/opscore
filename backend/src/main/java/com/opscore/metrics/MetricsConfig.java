package com.opscore.metrics;

import com.github.benmanes.caffeine.cache.Cache;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

/**
 * Configuration for custom Micrometer metrics.
 * 
 * Learning points:
 * - Gauges track current values that can go up or down
 * - MeterBinder allows registering metrics during Spring boot
 * - Cache metrics are automatically tracked by Caffeine integration
 * - Custom gauges can monitor any application state
 * 
 * What gets monitored:
 * - Cache statistics (size, hit rate, evictions)
 * - JVM metrics (memory, threads, GC)
 * - Database connection pool (HikariCP)
 * - HTTP request metrics (latency, status codes)
 * - Custom business metrics (active items, pending approvals)
 * 
 * Metric naming convention:
 * - Use dots or underscores: opscore.cache.size
 * - Start with application prefix: opscore.*
 * - Group by module: opscore.item.*, opscore.user.*
 * - Add meaningful tags for filtering
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class MetricsConfig {
    
    private final CacheManager cacheManager;
    
    /**
     * Register custom cache metrics as gauges.
     * Gauges show current values (unlike counters which only increase).
     */
    @Bean
    public MeterBinder cacheMetrics() {
        return (MeterRegistry registry) -> {
            Collection<String> cacheNames = cacheManager.getCacheNames();
            
            log.info("📊 Registering cache metrics for {} caches: {}", 
                    cacheNames.size(), cacheNames);
            
            for (String cacheName : cacheNames) {
                org.springframework.cache.Cache springCache = cacheManager.getCache(cacheName);
                
                if (springCache instanceof CaffeineCache caffeineCache) {
                    Cache<Object, Object> nativeCache = caffeineCache.getNativeCache();
                    
                    // Gauge: Current cache size
                    Gauge.builder("opscore.cache.size", nativeCache, Cache::estimatedSize)
                            .tag("cache", cacheName)
                            .description("Current number of entries in cache")
                            .register(registry);
                    
                    log.debug("Registered size gauge for cache: {}", cacheName);
                }
            }
            
            log.info("✅ Cache metrics registered successfully");
        };
    }
    
    /**
     * Example: Custom gauge for monitoring application state
     * In production, you might track:
     * - Number of active user sessions
     * - Pending workflow items
     * - Queue depths
     * - Available resources
     */
    @Bean
    public MeterBinder customBusinessMetrics() {
        return (MeterRegistry registry) -> {
            log.info("📊 Registering custom business metrics");
            
            // Example gauge that could track dynamic business values
            // In a real implementation, you'd inject services/repositories here
            
            Gauge.builder("opscore.system.uptime.seconds", 
                    () -> (System.currentTimeMillis() - startTime) / 1000.0)
                    .description("Application uptime in seconds")
                    .register(registry);
            
            log.info("✅ Custom business metrics registered");
        };
    }
    
    private final long startTime = System.currentTimeMillis();
}
