package com.opscore.event;

import com.opscore.item.Item;
import com.opscore.item.ItemStatus;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

/**
 * Event published when an item's status changes.
 * 
 * Use cases:
 * - Notify assigned user when status changes
 * - Update dashboard statistics
 * - Trigger next workflow step
 * - Send alerts for specific transitions
 */
@Getter
public class ItemStatusChangedEvent extends ApplicationEvent {
    
    private final UUID itemId;
    private final String itemCode;
    private final UUID tenantId;
    private final ItemStatus oldStatus;
    private final ItemStatus newStatus;
    private final UUID changedByUserId;
    
    public ItemStatusChangedEvent(Object source, Item item, ItemStatus oldStatus) {
        super(source);
        this.itemId = item.getId();
        this.itemCode = item.getCode();
        this.tenantId = item.getTenant().getId();
        this.oldStatus = oldStatus;
        this.newStatus = item.getStatus();
        this.changedByUserId = item.getCreatedByUser().getId(); // In real app, pass actual user
    }
}
