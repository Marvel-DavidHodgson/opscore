package com.opscore.item;

import com.opscore.event.ItemCreatedEvent;
import com.opscore.event.ItemStatusChangedEvent;
import com.opscore.exception.ResourceNotFoundException;
import com.opscore.tenant.Tenant;
import com.opscore.tenant.TenantRepository;
import com.opscore.user.User;
import com.opscore.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {

    private final ItemRepository itemRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    @Cacheable(value = "items", key = "#itemId + ':' + #tenantId")
    public Item getItemById(UUID itemId, UUID tenantId) {
        log.debug("Fetching item from database: {}", itemId);
        return itemRepository.findByIdAndTenant_Id(itemId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Item", itemId));
    }

    @Transactional(readOnly = true)
    public Page<Item> getItems(UUID tenantId, Pageable pageable) {
        return itemRepository.findByTenant_Id(tenantId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Item> getItemsWithFilters(UUID tenantId, ItemStatus status, String category, 
                                          UUID assignedToUserId, Pageable pageable) {
        String statusStr = status != null ? status.name() : null;
        return itemRepository.findByFilters(tenantId, statusStr, category, assignedToUserId, pageable);
    }

    @Transactional
    public Item createItem(UUID tenantId, UUID createdByUserId, String title, String description, 
                          String category, UUID assignedToUserId, Map<String, Object> metadata) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", tenantId));

        User createdByUser = userRepository.findById(createdByUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", createdByUserId));

        User assignedToUser = null;
        if (assignedToUserId != null) {
            assignedToUser = userRepository.findById(assignedToUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", assignedToUserId));
        }

        // Generate item code using tenant slug
        String code = generateItemCode(tenant.getSlug());

        Item item = Item.builder()
                .tenant(tenant)
                .code(code)
                .title(title)
                .description(description)
                .category(category)
                .status(ItemStatus.DRAFT)
                .assignedToUser(assignedToUser)
                .createdByUser(createdByUser)
                .metadata(metadata)
                .build();

        Item created = itemRepository.save(item);
        log.info("Item {} created by user {}", created.getCode(), createdByUser.getEmail());
        
        // Publish event for async processing
        eventPublisher.publishEvent(new ItemCreatedEvent(this, created));
        
        return created;
    }

    @Transactional
    @CacheEvict(value = "items", key = "#itemId + ':' + #tenantId")
    public Item updateItem(UUID itemId, UUID tenantId, String title, String description, 
                          String category, UUID assignedToUserId, Map<String, Object> metadata) {
        Item item = getItemById(itemId, tenantId);

        if (title != null) {
            item.setTitle(title);
        }

        if (description != null) {
            item.setDescription(description);
        }

        if (category != null) {
            item.setCategory(category);
        }

        if (assignedToUserId != null) {
            User assignedToUser = userRepository.findById(assignedToUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", assignedToUserId));
            item.setAssignedToUser(assignedToUser);
        }

        if (metadata != null) {
            item.setMetadata(metadata);
        }

        Item updated = itemRepository.save(item);
        log.info("Item {} updated", updated.getCode());
        return updated;
    }

    @Transactional
    @CacheEvict(value = "items", key = "#itemId + ':' + #tenantId")
    public void deleteItem(UUID itemId, UUID tenantId) {
        Item item = getItemById(itemId, tenantId);
        itemRepository.delete(item);
        log.info("Item {} deleted", item.getCode());
    }

    @Transactional
    public Item updateItemStatus(UUID itemId, UUID tenantId, ItemStatus newStatus) {
        Item item = getItemById(itemId, tenantId);
        ItemStatus oldStatus = item.getStatus();
        item.setStatus(newStatus);
        Item updated = itemRepository.save(item);
        log.info("Item {} status changed to {}", updated.getCode(), newStatus);
        
        // Publish event for async processing
        eventPublisher.publishEvent(new ItemStatusChangedEvent(this, updated, oldStatus));
        
        return updated;
    }

    private String generateItemCode(String tenantSlug) {
        // Simple implementation - in production, use database sequence or more sophisticated approach
        String prefix = tenantSlug.toUpperCase().substring(0, Math.min(4, tenantSlug.length()));
        long timestamp = System.currentTimeMillis() % 1000000;
        return String.format("%s-%06d", prefix, timestamp);
    }
}
