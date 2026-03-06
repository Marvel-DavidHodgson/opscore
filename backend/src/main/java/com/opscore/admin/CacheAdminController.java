package com.opscore.admin;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Admin endpoint for cache management.
 * 
 * Learning points:
 * - Administrative endpoints
 * - Cache inspection and management
 * - Runtime cache operations
 * - Security with role-based access
 * 
 * Operations:
 * - View cache statistics
 * - Clear specific caches
 * - Clear all caches
 * - Get cache details
 */
@RestController
@RequestMapping("/admin/cache")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin - Cache", description = "Cache management for administrators")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class CacheAdminController {
    
    private final CacheManager cacheManager;
    
    /**
     * Get statistics for all caches
     */
    @GetMapping("/stats")
    @Operation(summary = "Get cache statistics", 
               description = "View hit rates, sizes, and performance metrics for all caches")
    public ResponseEntity<Map<String, CacheStatsDto>> getCacheStatistics() {
        log.info("Admin requested cache statistics");
        
        Map<String, CacheStatsDto> stats = new HashMap<>();
        
        cacheManager.getCacheNames().forEach(cacheName -> {
            var cache = cacheManager.getCache(cacheName);
            if (cache instanceof CaffeineCache caffeineCache) {
                Cache<Object, Object> nativeCache = caffeineCache.getNativeCache();
                CacheStats cacheStats = nativeCache.stats();
                
                CacheStatsDto dto = new CacheStatsDto(
                        cacheName,
                        nativeCache.estimatedSize(),
                        cacheStats.hitCount(),
                        cacheStats.missCount(),
                        cacheStats.hitRate() * 100,
                        cacheStats.missRate() * 100,
                        cacheStats.evictionCount(),
                        cacheStats.loadSuccessCount(),
                        cacheStats.loadFailureCount(),
                        cacheStats.averageLoadPenalty() / 1_000_000.0 // Convert to ms
                );
                
                stats.put(cacheName, dto);
            }
        });
        
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Get details for a specific cache
     */
    @GetMapping("/{cacheName}")
    @Operation(summary = "Get cache details", 
               description = "View detailed information about a specific cache")
    public ResponseEntity<CacheStatsDto> getCacheDetails(@PathVariable String cacheName) {
        log.info("Admin requested details for cache: {}", cacheName);
        
        var cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return ResponseEntity.notFound().build();
        }
        
        if (cache instanceof CaffeineCache caffeineCache) {
            Cache<Object, Object> nativeCache = caffeineCache.getNativeCache();
            CacheStats stats = nativeCache.stats();
            
            CacheStatsDto dto = new CacheStatsDto(
                    cacheName,
                    nativeCache.estimatedSize(),
                    stats.hitCount(),
                    stats.missCount(),
                    stats.hitRate() * 100,
                    stats.missRate() * 100,
                    stats.evictionCount(),
                    stats.loadSuccessCount(),
                    stats.loadFailureCount(),
                    stats.averageLoadPenalty() / 1_000_000.0
            );
            
            return ResponseEntity.ok(dto);
        }
        
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Clear a specific cache
     */
    @DeleteMapping("/{cacheName}")
    @Operation(summary = "Clear cache", 
               description = "Remove all entries from a specific cache")
    public ResponseEntity<Map<String, String>> clearCache(@PathVariable String cacheName) {
        log.warn("Admin clearing cache: {}", cacheName);
        
        var cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return ResponseEntity.notFound().build();
        }
        
        cache.clear();
        log.info("Cache '{}' cleared successfully", cacheName);
        
        return ResponseEntity.ok(Map.of(
                "message", "Cache cleared successfully",
                "cacheName", cacheName
        ));
    }
    
    /**
     * Clear all caches
     */
    @DeleteMapping
    @Operation(summary = "Clear all caches", 
               description = "Remove all entries from all caches")
    public ResponseEntity<Map<String, Object>> clearAllCaches() {
        log.warn("Admin clearing ALL caches");
        
        cacheManager.getCacheNames().forEach(cacheName -> {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
                log.info("Cleared cache: {}", cacheName);
            }
        });
        
        return ResponseEntity.ok(Map.of(
                "message", "All caches cleared successfully",
                "clearedCaches", cacheManager.getCacheNames()
        ));
    }
    
    /**
     * DTO for cache statistics
     */
    public record CacheStatsDto(
            String name,
            long size,
            long hitCount,
            long missCount,
            double hitRate,
            double missRate,
            long evictionCount,
            long loadSuccessCount,
            long loadFailureCount,
            double averageLoadTimeMs
    ) {}
}
