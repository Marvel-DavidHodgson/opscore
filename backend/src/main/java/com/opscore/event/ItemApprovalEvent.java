package com.opscore.event;

import com.opscore.item.ItemStatus;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

/**
 * Event published when an item is approved or rejected.
 * 
 * Use cases:
 * - Send approval notification emails
 * - Update item creator
 * - Log in audit trail
 * - Trigger post-approval workflows
 */
@Getter
public class ItemApprovalEvent extends ApplicationEvent {
    
    private final UUID itemId;
    private final String itemCode;
    private final UUID tenantId;
    private final ItemStatus approvalStatus; // APPROVED or REJECTED
    private final UUID approverUserId;
    private final String approverEmail;
    private final String comment;
    
    public ItemApprovalEvent(Object source, UUID itemId, String itemCode, UUID tenantId,
                            ItemStatus approvalStatus, UUID approverUserId, 
                            String approverEmail, String comment) {
        super(source);
        this.itemId = itemId;
        this.itemCode = itemCode;
        this.tenantId = tenantId;
        this.approvalStatus = approvalStatus;
        this.approverUserId = approverUserId;
        this.approverEmail = approverEmail;
        this.comment = comment;
    }
}
