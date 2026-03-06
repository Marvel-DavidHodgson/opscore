package com.opscore.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Service for tracking custom business metrics using Micrometer.
 * 
 * Learning points:
 * - Micrometer provides vendor-neutral metric instrumentation
 * - Metrics are exported via /actuator/metrics endpoint
 * - Supports multiple monitoring backends (Prometheus, Datadog, etc.)
 * - Counter: monotonically increasing value (items created, logins)
 * - Gauge: value that can go up or down (active users, cache size)
 * - Timer: measure duration and count of events
 * - Tags/labels allow filtering and grouping metrics
 * 
 * Metric Types:
 * - Counter: For counting events that ONLY increase (never decrease)
 * - Gauge: For values that fluctuate (current values, not cumulative)
 * - Timer: For timing operations and calculating percentiles
 * - Distribution Summary: For tracking distribution of values
 * 
 * Best Practices:
 * - Use consistent naming: module.component.action (e.g., opscore.item.created)
 * - Add meaningful tags (tenant, status, category)
 * - Don't create unbounded tags (no user IDs or unique identifiers)
 * - Keep metric names lowercase with dots or underscores
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MetricsService {
    
    private final MeterRegistry meterRegistry;
    
    // ==================== Item Metrics ====================
    
    /**
     * Increment counter when an item is created
     */
    public void recordItemCreated(UUID tenantId, String category) {
        Counter.builder("opscore.item.created")
                .tag("tenant_id", tenantId.toString())
                .tag("category", category != null ? category : "none")
                .description("Total number of items created")
                .register(meterRegistry)
                .increment();
        
        log.debug("📊 Metric: Item created (tenant={}, category={})", tenantId, category);
    }
    
    /**
     * Increment counter for status transitions
     */
    public void recordItemStatusChange(UUID tenantId, String fromStatus, String toStatus) {
        Counter.builder("opscore.item.status.transition")
                .tag("tenant_id", tenantId.toString())
                .tag("from_status", fromStatus)
                .tag("to_status", toStatus)
                .description("Item status transitions")
                .register(meterRegistry)
                .increment();
        
        log.debug("📊 Metric: Status transition (tenant={}, {} → {})", tenantId, fromStatus, toStatus);
    }
    
    /**
     * Record approval decision
     */
    public void recordApprovalDecision(UUID tenantId, boolean approved) {
        String decision = approved ? "approved" : "rejected";
        
        Counter.builder("opscore.item.approval")
                .tag("tenant_id", tenantId.toString())
                .tag("decision", decision)
                .description("Item approval decisions")
                .register(meterRegistry)
                .increment();
        
        log.debug("📊 Metric: Approval {} (tenant={})", decision, tenantId);
    }
    
    /**
     * Time how long an operation takes
     */
    public void recordOperationDuration(String operationName, long durationMs) {
        Timer.builder("opscore.operation.duration")
                .tag("operation", operationName)
                .description("Operation execution duration")
                .register(meterRegistry)
                .record(durationMs, TimeUnit.MILLISECONDS);
        
        log.debug("📊 Metric: Operation '{}' took {}ms", operationName, durationMs);
    }
    
    // ==================== User Metrics ====================
    
    /**
     * Record user login
     */
    public void recordUserLogin(UUID tenantId, boolean successful) {
        String status = successful ? "success" : "failure";
        
        Counter.builder("opscore.user.login")
                .tag("tenant_id", tenantId.toString())
                .tag("status", status)
                .description("User login attempts")
                .register(meterRegistry)
                .increment();
        
        log.debug("📊 Metric: Login {} (tenant={})", status, tenantId);
    }
    
    /**
     * Record user creation
     */
    public void recordUserCreated(UUID tenantId, String role) {
        Counter.builder("opscore.user.created")
                .tag("tenant_id", tenantId.toString())
                .tag("role", role)
                .description("Users created")
                .register(meterRegistry)
                .increment();
        
        log.debug("📊 Metric: User created (tenant={}, role={})", tenantId, role);
    }
    
    // ==================== Cache Metrics ====================
    
    /**
     * Record cache hit
     */
    public void recordCacheHit(String cacheName) {
        Counter.builder("opscore.cache.operation")
                .tag("cache", cacheName)
                .tag("result", "hit")
                .description("Cache operations")
                .register(meterRegistry)
                .increment();
    }
    
    /**
     * Record cache miss
     */
    public void recordCacheMiss(String cacheName) {
        Counter.builder("opscore.cache.operation")
                .tag("cache", cacheName)
                .tag("result", "miss")
                .description("Cache operations")
                .register(meterRegistry)
                .increment();
    }
    
    /**
     * Record cache eviction
     */
    public void recordCacheEviction(String cacheName) {
        Counter.builder("opscore.cache.eviction")
                .tag("cache", cacheName)
                .description("Cache evictions")
                .register(meterRegistry)
                .increment();
    }
    
    // ==================== API Metrics ====================
    
    /**
     * Record API call with status
     */
    public void recordApiCall(String endpoint, String method, int statusCode) {
        Counter.builder("opscore.api.calls")
                .tag("endpoint", endpoint)
                .tag("method", method)
                .tag("status", String.valueOf(statusCode))
                .description("API calls")
                .register(meterRegistry)
                .increment();
    }
    
    /**
     * Record API error
     */
    public void recordApiError(String endpoint, String errorType) {
        Counter.builder("opscore.api.errors")
                .tag("endpoint", endpoint)
                .tag("error_type", errorType)
                .description("API errors")
                .register(meterRegistry)
                .increment();
    }
    
    // ==================== Business Metrics ====================
    
    /**
     * Increment a generic business counter
     */
    public void incrementBusinessCounter(String metricName, String... tags) {
        if (tags.length % 2 != 0) {
            log.error("Tags must be key-value pairs");
            return;
        }
        
        Counter.Builder builder = Counter.builder("opscore.business." + metricName);
        
        // Add tags in pairs
        for (int i = 0; i < tags.length; i += 2) {
            builder.tag(tags[i], tags[i + 1]);
        }
        
        builder.register(meterRegistry).increment();
    }
    
    /**
     * Record timing for a business operation
     */
    public void recordBusinessTiming(String operation, long durationMs, String... tags) {
        if (tags.length % 2 != 0) {
            log.error("Tags must be key-value pairs");
            return;
        }
        
        Timer.Builder builder = Timer.builder("opscore.business.duration")
                .tag("operation", operation);
        
        // Add tags in pairs
        for (int i = 0; i < tags.length; i += 2) {
            builder.tag(tags[i], tags[i + 1]);
        }
        
        builder.register(meterRegistry).record(durationMs, TimeUnit.MILLISECONDS);
    }
}
