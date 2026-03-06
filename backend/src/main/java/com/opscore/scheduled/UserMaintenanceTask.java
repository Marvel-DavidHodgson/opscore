package com.opscore.scheduled;

import com.opscore.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Scheduled task for user-related maintenance.
 * 
 * Tasks:
 * - Identify inactive users
 * - Send reminder emails
 * - Clean up unverified accounts (if email verification implemented)
 * - Generate user activity reports
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserMaintenanceTask {
    
    private final UserRepository userRepository;
    
    /**
     * Check for inactive users weekly on Sundays at 10 AM.
     * 
     * cron: "0 0 10 ? * SUN" = 10:00 AM every Sunday
     * 
     * Definition of inactive:
     * - No login in past 30 days
     * - Account is still active (not deactivated)
     */
    @Scheduled(cron = "0 0 10 ? * SUN")
    public void identifyInactiveUsers() {
        log.info("🔍 Identifying inactive users...");
        
        try {
            Instant thirtyDaysAgo = Instant.now().minus(30, ChronoUnit.DAYS);
            
            // Find users who haven't logged in for 30 days
            var inactiveUsers = userRepository.findByIsActiveTrueAndLastLoginAtBefore(thirtyDaysAgo);
            
            if (!inactiveUsers.isEmpty()) {
                log.info("Found {} inactive users (no login in 30+ days)", inactiveUsers.size());
                
                // In production:
                // - Send reminder emails
                // - Notify admins
                // - Consider automatic deactivation after 90 days
                
                inactiveUsers.forEach(user -> 
                    log.debug("Inactive user: {} (last login: {})", 
                        user.getEmail(), user.getLastLoginAt())
                );
            } else {
                log.info("All users are active");
            }
            
        } catch (Exception e) {
            log.error("❌ Failed to identify inactive users", e);
        }
    }
    
    /**
     * Generate monthly user statistics on the 1st of each month at 8 AM.
     * 
     * cron: "0 0 8 1 * ?" = 8:00 AM on the 1st day of every month
     */
    @Scheduled(cron = "0 0 8 1 * ?")
    public void generateMonthlyUserStats() {
        log.info("📊 Generating monthly user statistics...");
        
        try {
            // Count active users by tenant
            var tenants = userRepository.findAll().stream()
                    .collect(java.util.stream.Collectors.groupingBy(
                            user -> user.getTenant().getName(),
                            java.util.stream.Collectors.counting()
                    ));
            
            log.info("Monthly user statistics:");
            tenants.forEach((tenant, count) -> 
                log.info("  Tenant '{}': {} users", tenant, count)
            );
            
            // In production:
            // - Send to analytics platform
            // - Generate PDF report
            // - Email to stakeholders
            
        } catch (Exception e) {
            log.error("❌ Failed to generate user statistics", e);
        }
    }
}
