package com.opscore.metrics;

import io.micrometer.core.instrument.Timer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * HTTP filter to measure API request duration and track metrics.
 * 
 * Learning points:
 * - Filters intercept all HTTP requests before they reach controllers
 * - Timer.Sample measures elapsed time accurately
 * - OncePerRequestFilter ensures filter runs exactly once per request
 * - Can track response status, request path, HTTP method
 * 
 * Metrics tracked:
 * - Request duration (percentiles: p50, p95, p99)
 * - Request count by endpoint and status
 * - Error rates by endpoint
 * 
 * Use cases:
 * - Identify slow endpoints
 * - Monitor error rates
 * - Track API usage patterns
 * - SLA monitoring
 * 
 * Integration:
 * - Metrics available at /actuator/metrics/http.server.requests
 * - Can be exported to Prometheus, Grafana, etc.
 * - Supports alerting on high latency or error rates
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MetricsFilter extends OncePerRequestFilter {
    
    private final MetricsService metricsService;
    
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        // Start timing
        long startTime = System.currentTimeMillis();
        
        try {
            // Continue the request chain
            filterChain.doFilter(request, response);
            
        } finally {
            // Record metrics after request completes
            recordMetrics(request, response, startTime);
        }
    }
    
    private void recordMetrics(HttpServletRequest request, HttpServletResponse response, long startTime) {
        try {
            long duration = System.currentTimeMillis() - startTime;
            String method = request.getMethod();
            String uri = getSimplifiedUri(request.getRequestURI());
            int status = response.getStatus();
            
            // Record API call metric
            metricsService.recordApiCall(uri, method, status);
            
            // Record operation duration
            metricsService.recordOperationDuration(
                    String.format("%s %s", method, uri), 
                    duration
            );
            
            // Log slow requests (over 1 second)
            if (duration > 1000) {
                log.warn("⚠️ Slow request: {} {} took {}ms (status: {})", 
                        method, uri, duration, status);
            }
            
            // Record errors (4xx, 5xx)
            if (status >= 400) {
                String errorType = status >= 500 ? "server_error" : "client_error";
                metricsService.recordApiError(uri, errorType);
                
                log.debug("📊 Recorded error metric: {} {} - {} ({})", 
                        method, uri, status, errorType);
            }
            
        } catch (Exception e) {
            // Don't let metrics recording affect the actual request
            log.error("Failed to record request metrics", e);
        }
    }
    
    /**
     * Simplify URI to avoid unbounded metric cardinality.
     * Replace IDs with placeholders to group similar endpoints.
     * 
     * Examples:
     * /api/items/123e4567-e89b-12d3-a456-426614174000 -> /api/items/{id}
     * /api/users/456/items -> /api/users/{id}/items
     */
    private String getSimplifiedUri(String uri) {
        if (uri == null) {
            return "unknown";
        }
        
        // Remove /api prefix for cleaner metrics
        String simplified = uri.startsWith("/api") ? uri.substring(4) : uri;
        
        // Replace UUIDs with {id}
        simplified = simplified.replaceAll(
                "/[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}",
                "/{id}"
        );
        
        // Replace numeric IDs with {id}
        simplified = simplified.replaceAll("/\\d+", "/{id}");
        
        // Remove query parameters
        int queryIndex = simplified.indexOf('?');
        if (queryIndex != -1) {
            simplified = simplified.substring(0, queryIndex);
        }
        
        return simplified.isEmpty() ? "/" : simplified;
    }
    
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // Don't track metrics for actuator endpoints (avoid recursion)
        // Don't track static resources
        return path.startsWith("/actuator") || 
               path.startsWith("/swagger-ui") ||
               path.startsWith("/api-docs") ||
               path.contains("webjars");
    }
}
