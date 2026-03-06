package com.opscore.scheduled;

import com.opscore.audit.AuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Scheduled task for audit log maintenance.
 * 
 * Learning points:
 * - Batch data cleanup operations
 * - Transaction management in scheduled tasks
 * - Data retention policies
 * - Performance considerations for bulk operations
 * 
 * Best practices:
 * - Run during off-peak hours
 * - Use batch processing for large datasets
 * - Log cleanup results
 * - Monitor execution time
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditMaintenanceTask {
    
    private final AuditRepository auditRepository;
    
    /**
     * Clean up old audit logs daily at 3 AM.
     * 
     * cron: "0 0 3 * * ?" = 3:00 AM every day
     * 
     * Retention policy:
     * - Keep audit logs for 90 days
     * - Archive older logs (not implemented here)
     * - Delete after archival
     */
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void cleanupOldAuditLogs() {
        log.info("🧹 Starting audit log cleanup...");
        
        try {
            // Calculate cutoff date (90 days ago)
            Instant cutoffDate = Instant.now().minus(90, ChronoUnit.DAYS);
            
            // Count logs to be deleted
            long count = auditRepository.countByCreatedAtBefore(cutoffDate);
            
            if (count > 0) {
                log.info("Found {} audit logs older than 90 days", count);
                
                // In production, you would:
                // 1. Archive to cold storage (S3, etc.)
                // 2. Delete from database
                // auditRepository.deleteByCreatedAtBefore(cutoffDate);
                
                log.info("✅ Would delete {} old audit logs (dry run mode)", count);
            } else {
                log.info("No old audit logs to clean up");
            }
            
        } catch (Exception e) {
            log.error("❌ Failed to clean up audit logs", e);
        }
    }
    
    /**
     * Generate audit statistics report weekly on Mondays at 9 AM.
     * 
     * cron: "0 0 9 ? * MON" = 9:00 AM every Monday
     */
    @Scheduled(cron = "0 0 9 ? * MON")
    @Transactional(readOnly = true)
    public void generateWeeklyAuditReport() {
        log.info("📊 Generating weekly audit report...");
        
        try {
            Instant oneWeekAgo = Instant.now().minus(7, ChronoUnit.DAYS);
            long weeklyCount = auditRepository.countByCreatedAtAfter(oneWeekAgo);
            
            log.info("Weekly audit activity: {} events logged in the past 7 days", weeklyCount);
            
            // In production:
            // - Group by tenant, action type, user
            // - Send email report to admins
            // - Update dashboard metrics
            
        } catch (Exception e) {
            log.error("❌ Failed to generate audit report", e);
        }
    }
}
