package com.opscore.event;

import com.opscore.item.Item;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

/**
 * Event published when a new item is created.
 * 
 * Learning points:
 * - Domain events represent something that happened in the domain
 * - Extends ApplicationEvent (Spring's base event class)
 * - Immutable - events are facts that already occurred
 * - Contains all relevant data listeners might need
 * - Decouples business logic from side effects
 * 
 * Example: When item is created, we might want to:
 * - Send notification
 * - Update statistics
 * - Log audit trail
 * - Trigger workflow
 * 
 * Events allow these actions without coupling them to ItemService
 */
@Getter
public class ItemCreatedEvent extends ApplicationEvent {
    
    private final UUID itemId;
    private final String itemCode;
    private final String title;
    private final String category;
    private final UUID tenantId;
    private final UUID createdByUserId;
    private final String createdByEmail;
    
    public ItemCreatedEvent(Object source, Item item) {
        super(source);
        this.itemId = item.getId();
        this.itemCode = item.getCode();
        this.title = item.getTitle();
        this.category = item.getCategory();
        this.tenantId = item.getTenant().getId();
        this.createdByUserId = item.getCreatedByUser().getId();
        this.createdByEmail = item.getCreatedByUser().getEmail();
    }
}
