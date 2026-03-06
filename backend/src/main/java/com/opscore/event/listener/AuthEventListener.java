package com.opscore.event.listener;

import com.opscore.event.UserLoginEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Event listener for authentication and user-related events.
 * 
 * Handles:
 * - Login tracking
 * - Security auditing
 * - User activity analytics
 * 
 * Uses @TransactionalEventListener to ensure events only fire after successful commits
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthEventListener {
    
    /**
     * Track user login for security monitoring.
     * Runs async to not slow down login response.
     * 
     * @TransactionalEventListener ensures login is only tracked after successful commit.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleUserLogin(UserLoginEvent event) {
        log.info("🔐 [ASYNC] User login event: {} (tenant: {}, IP: {})",
                event.getEmail(), event.getTenantId(), event.getIpAddress());
        
        try {
            // In production:
            // - Log to security audit system
            // - Check for suspicious login patterns
            // - Update user analytics
            // - Send login alert email (if enabled)
            // - Track session for analytics
            
            log.info("📝 Recording login activity for user {} from IP {}", 
                    event.getEmail(), event.getIpAddress());
            
            // Simulate recording
            Thread.sleep(50);
            
            log.info("✅ Login activity recorded for {}", event.getEmail());
            
        } catch (Exception e) {
            log.error("❌ Failed to process login event for {}", event.getEmail(), e);
        }
    }
}
