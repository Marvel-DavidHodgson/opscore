package com.opscore.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Spring Cache configuration using Caffeine.
 * 
 * Learning points:
 * - @EnableCaching activates Spring's cache annotations
 * - Caffeine is a high-performance in-memory cache
 * - Cache expiration strategies (time-based, size-based)
 * - Cache naming and isolation
 * - Memory management for caches
 * 
 * Cache usage:
 * - @Cacheable - Cache method results
 * - @CacheEvict - Remove from cache
 * - @CachePut - Update cache
 * - @Caching - Combine multiple cache operations
 * 
 * Why Caffeine:
 * - Fast (successor to Guava cache)
 * - Memory efficient
 * - Built-in statistics
 * - Thread-safe
 * - Spring Boot native support
 */
@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {
    
    /**
     * Configure cache manager with Caffeine
     * 
     * Cache names used in application:
     * - tenants: Tenant configuration (rarely changes)
     * - users: User information (changes occasionally)
     * - items: Item details (changes frequently)
     * - reports: Generated reports (expensive to compute)
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "tenants",    // Tenant data cache
                "users",      // User data cache
                "items",      // Item data cache
                "reports"     // Report data cache
        );
        
        // Configure default cache settings
        cacheManager.setCaffeine(caffeineCacheBuilder());
        
        log.info("Caffeine cache manager configured with caches: tenants, users, items, reports");
        
        return cacheManager;
    }
    
    /**
     * Configure Caffeine cache builder with settings
     * 
     * Settings:
     * - maximumSize: Max entries in cache (prevents memory overflow)
     * - expireAfterWrite: Time-to-live for cache entries
     * - recordStats: Enable cache statistics (hit rate, miss rate, etc)
     */
    private Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                // Maximum entries per cache
                .maximumSize(1000)
                
                // Expire entries 10 minutes after write
                .expireAfterWrite(10, TimeUnit.MINUTES)
                
                // Expire entries 5 minutes after last access
                .expireAfterAccess(5, TimeUnit.MINUTES)
                
                // Record cache statistics
                .recordStats()
                
                // Weak keys allow garbage collection
                .weakKeys();
    }
    
    /**
     * Create a specific cache configuration for tenant data.
     * Tenants rarely change, so we can cache longer.
     */
    @Bean
    public Caffeine<Object, Object> tenantCacheConfig() {
        return Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .recordStats();
    }
    
    /**
     * Create a specific cache configuration for reports.
     * Reports are expensive to generate, cache longer.
     */
    @Bean
    public Caffeine<Object, Object> reportCacheConfig() {
        return Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .recordStats();
    }
}
