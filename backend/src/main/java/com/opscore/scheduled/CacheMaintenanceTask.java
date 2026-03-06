package com.opscore.scheduled;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled task for cache monitoring and maintenance.
 * 
 * Learning points:
 * - @Scheduled annotation with various strategies
 * - Cron expressions for time-based scheduling
 * - Cache statistics monitoring
 * - Performance metrics tracking
 * - Proactive cache management
 * 
 * Scheduled tasks run:
 * - Cache statistics logging (every 5 minutes)
 * - Cache eviction analysis (every hour)
 * - Cache warmup (daily at 6 AM)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CacheMaintenanceTask {
    
    private final CacheManager cacheManager;
    
    /**
     * Log cache statistics every 5 minutes.
     * 
     * fixedRate: Runs every 5 minutes from start of previous execution
     * initialDelay: Wait 1 minute before first execution
     * 
     * Monitors:
     * - Hit rate (% of requests served from cache)
     * - Miss rate (% of requests needing database lookup)
     * - Eviction count (entries removed due to size/time limits)
     */
    @Scheduled(fixedRate = 300000, initialDelay = 60000) // Every 5 minutes
    public void logCacheStatistics() {
        log.info("📊 Cache Statistics Report:");
        
        cacheManager.getCacheNames().forEach(cacheName -> {
            var cache = cacheManager.getCache(cacheName);
            if (cache instanceof CaffeineCache caffeineCache) {
                Cache<Object, Object> nativeCache = caffeineCache.getNativeCache();
                CacheStats stats = nativeCache.stats();
                
                double hitRate = stats.hitRate() * 100;
                double missRate = stats.missRate() * 100;
                
                log.info("  Cache '{}': size={}, hits={}, misses={}, hitRate={:.2f}%, evictions={}",
                        cacheName,
                        nativeCache.estimatedSize(),
                        stats.hitCount(),
                        stats.missCount(),
                        hitRate,
                        stats.evictionCount());
                
                // Warn if hit rate is low
                if (hitRate < 50 && stats.requestCount() > 100) {
                    log.warn("⚠️ Low cache hit rate for '{}': {:.2f}% (consider adjusting cache config)",
                            cacheName, hitRate);
                }
            }
        });
    }
    
    /**
     * Analyze cache performance every hour.
     * 
     * cron: "0 0 * * * ?" = Every hour at the top of the hour
     * Format: second minute hour day month weekday
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void analyzeCachePerformance() {
        log.info("🔍 Analyzing cache performance...");
        
        cacheManager.getCacheNames().forEach(cacheName -> {
            var cache = cacheManager.getCache(cacheName);
            if (cache instanceof CaffeineCache caffeineCache) {
                Cache<Object, Object> nativeCache = caffeineCache.getNativeCache();
                CacheStats stats = nativeCache.stats();
                
                long totalRequests = stats.requestCount();
                if (totalRequests > 0) {
                    double avgLoadPenalty = stats.averageLoadPenalty() / 1_000_000; // Convert to ms
                    
                    log.info("  Cache '{}' analysis: requests={}, avgLoadTime={:.2f}ms, totalLoadTime={:.2f}s",
                            cacheName,
                            totalRequests,
                            avgLoadPenalty,
                            stats.totalLoadTime() / 1_000_000_000.0);
                }
            }
        });
    }
    
    /**
     * Clear and warm up caches daily at 6 AM.
     * 
     * cron: "0 0 6 * * ?" = 6:00 AM every day
     * 
     * Useful for:
     * - Resetting cache after bulk data changes
     * - Preloading frequently accessed data
     * - Clearing stale entries
     */
    @Scheduled(cron = "0 0 6 * * ?")
    public void dailyCacheWarmup() {
        log.info("🔄 Starting daily cache warmup...");
        
        // In production, you might:
        // 1. Clear all caches
        // 2. Preload tenant data
        // 3. Preload active user data
        // 4. Preload recent items
        
        log.info("✅ Daily cache warmup completed");
    }
}
