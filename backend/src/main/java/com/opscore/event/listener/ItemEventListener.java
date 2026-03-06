package com.opscore.event.listener;

import com.opscore.event.ItemApprovalEvent;
import com.opscore.event.ItemCreatedEvent;
import com.opscore.event.ItemStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Event listener for item-related events.
 * 
 * Learning points:
 * - @TransactionalEventListener ensures events fire only after transaction commits
 * - @Async makes the listener run in a separate thread (non-blocking)
 * - Multiple listeners can handle the same event
 * - Listeners are loosely coupled from event publishers
 * - Transaction boundary matters: events published after transaction commits
 * 
 * Use cases:
 * - Send notifications
 * - Update caches
 * - Generate reports
 * - Call external APIs
 * 
 * Best practices:
 * - Keep listeners fast and simple
 * - Use @Async for I/O operations (email, HTTP calls)
 * - Handle exceptions gracefully (use try-catch)
 * - Don't modify database in @Async listeners unless using new transaction
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ItemEventListener {
    
    /**
     * Handle item creation - send notifications, update statistics, etc.
     * Runs asynchronously so it doesn't block the main thread.
     * 
     * @TransactionalEventListener ensures this only runs after the transaction commits.
     * If the transaction rolls back, this listener will NOT be called.
     * This prevents sending notifications for items that were never saved!
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleItemCreated(ItemCreatedEvent event) {
        log.info("🎉 [ASYNC] Item created event received: {} - {} (tenant: {})",
                event.getItemCode(), event.getTitle(), event.getTenantId());
        
        try {
            // Simulate notification sending
            log.info("📧 Sending creation notification for item {} to user {}",
                    event.getItemCode(), event.getCreatedByEmail());
            
            // In production, you would:
            // - Send email via EmailService
            // - Send push notification
            // - Update real-time dashboard via WebSocket
            // - Call external webhook
            
            Thread.sleep(100); // Simulate I/O delay
            log.info("✅ Notification sent successfully for item {}", event.getItemCode());
            
        } catch (Exception e) {
            log.error("❌ Failed to process item created event for {}", event.getItemCode(), e);
            // In production: retry logic, dead letter queue, alert admins
        }
    }
    
    /**
     * Handle status changes - update dashboards, send alerts
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleItemStatusChanged(ItemStatusChangedEvent event) {
        log.info("🔄 [ASYNC] Item status changed: {} ({} → {})",
                event.getItemCode(), event.getOldStatus(), event.getNewStatus());
        
        try {
            // Update real-time statistics
            log.info("📊 Updating dashboard statistics for tenant {}", event.getTenantId());
            
            // Check if this transition needs special handling
            if (event.getNewStatus().toString().equals("PENDING")) {
                log.info("⏰ Item {} is pending approval - notifying managers", event.getItemCode());
                // Send notification to managers
            }
            
            Thread.sleep(50); // Simulate processing
            
        } catch (Exception e) {
            log.error("❌ Failed to handle status change for {}", event.getItemCode(), e);
        }
    }
    
    /**
     * Handle approval/rejection - send detailed notifications with comments
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleItemApproval(ItemApprovalEvent event) {
        log.info("✍️ [ASYNC] Item approval event: {} - {} by {}",
                event.getItemCode(), event.getApprovalStatus(), event.getApproverEmail());
        
        try {
            String action = event.getApprovalStatus().toString().equals("APPROVED") ? "approved" : "rejected";
            
            log.info("📧 Sending {} notification for item {} to item creator", action, event.getItemCode());
            
            if (event.getComment() != null) {
                log.info("💬 Approver comment: {}", event.getComment());
            }
            
            // In production:
            // - Email item creator with approval decision
            // - Include approver comments
            // - Update approval workflow status
            // - Trigger next workflow step
            
            Thread.sleep(150); // Simulate email sending
            log.info("✅ Approval notification sent for {}", event.getItemCode());
            
        } catch (Exception e) {
            log.error("❌ Failed to handle approval event for {}", event.getItemCode(), e);
        }
    }
}
