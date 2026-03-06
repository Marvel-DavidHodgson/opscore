package com.opscore.event.listener;

import com.opscore.event.ItemApprovalEvent;
import com.opscore.event.ItemCreatedEvent;
import com.opscore.event.ItemStatusChangedEvent;
import com.opscore.event.UserLoginEvent;
import com.opscore.metrics.MetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Analytics event listener - tracks metrics and statistics using Micrometer.
 * 
 * This listener demonstrates:
 * - Multiple listeners can handle the same events
 * - Different concerns separated into different listeners
 * - Async processing for non-critical operations
 * - @TransactionalEventListener ensures metrics only recorded after successful commits
 * - Integration with Micrometer for standardized metrics collection
 * 
 * In production, this would:
 * - Send metrics to monitoring systems (Prometheus, Datadog, etc.)
 * - Update real-time dashboards
 * - Calculate business KPIs
 * - Feed data to analytics pipelines
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AnalyticsEventListener {
    
    private final MetricsService metricsService;
    
    /**
     * Track item creation metrics
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void trackItemCreated(ItemCreatedEvent event) {
        log.info("📊 [ANALYTICS] Recording item creation metric for tenant {}", event.getTenantId());
        
        try {
            // Record metric using Micrometer
            metricsService.recordItemCreated(event.getTenantId(), event.getCategory());
            
            // Additional analytics could include:
            // - Update dashboard statistics
            // - Calculate creation rate
            // - Track category distribution
            
        } catch (Exception e) {
            log.error("Failed to record item creation metric", e);
        }
    }
    
    /**
     * Track status transition metrics
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void trackStatusChange(ItemStatusChangedEvent event) {
        log.info("📊 [ANALYTICS] Recording status transition: {} → {} for tenant {}",
                event.getOldStatus(), event.getNewStatus(), event.getTenantId());
        
        try {
            // Record the status transition metric
            metricsService.recordItemStatusChange(
                event.getTenantId(),
                event.getOldStatus().toString(),
                event.getNewStatus().toString()
            );
            
            // Additional analytics:
            // - Track workflow completion times
            // - Calculate bottlenecks
            // - Monitor approval rates
            // - Generate workflow analytics
            
        } catch (Exception e) {
            log.error("Failed to record status change metric", e);
        }
    }
    
    /**
     * Track approval metrics
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void trackApproval(ItemApprovalEvent event) {
        boolean isApproved = event.getApprovalStatus().toString().equals("APPROVED");
        String metric = isApproved ? "approvals" : "rejections";
        log.info("📊 [ANALYTICS] Recording {} metric for tenant {}", metric, event.getTenantId());
        
        try {
            // Record approval decision metric
            metricsService.recordApprovalDecision(event.getTenantId(), isApproved);
            
            // Additional analytics:
            // - Track approval/rejection rates
            // - Calculate average approval time
            // - Monitor approver performance
            // - Generate approval reports
            
        } catch (Exception e) {
            log.error("Failed to record approval metric", e);
        }
    }
    
    /**
     * Track user activity
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void trackUserLogin(UserLoginEvent event) {
        log.info("📊 [ANALYTICS] Recording user activity for tenant {}", event.getTenantId());
        
        try {
            // Record login metric (successful login)
            metricsService.recordUserLogin(event.getTenantId(), true);
            
            // Additional analytics:
            // - Track active users
            // - Calculate login frequency
            // - Monitor user engagement
            // - Detect inactive accounts
            
        } catch (Exception e) {
            log.error("Failed to record login metric", e);
        }
    }
}
