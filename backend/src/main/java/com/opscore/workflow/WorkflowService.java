package com.opscore.workflow;

import com.opscore.event.ItemApprovalEvent;
import com.opscore.event.ItemStatusChangedEvent;
import com.opscore.exception.BusinessValidationException;
import com.opscore.exception.ForbiddenException;
import com.opscore.exception.ResourceNotFoundException;
import com.opscore.item.Item;
import com.opscore.item.ItemRepository;
import com.opscore.item.ItemStatus;
import com.opscore.user.Role;
import com.opscore.user.User;
import com.opscore.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ApprovalRepository approvalRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Item submitForApproval(UUID itemId, UUID tenantId, UUID actorUserId, String comment) {
        Item item = itemRepository.findByIdAndTenant_Id(itemId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Item", itemId));

        User actorUser = userRepository.findById(actorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", actorUserId));

        if (item.getStatus() != ItemStatus.DRAFT && item.getStatus() != ItemStatus.REJECTED) {
            throw new BusinessValidationException("Item can only be submitted from DRAFT or REJECTED status");
        }

        ItemStatus oldStatus = item.getStatus();
        item.setStatus(ItemStatus.PENDING);
        Item updated = itemRepository.save(item);

        // Create approval event
        ApprovalEvent event = ApprovalEvent.builder()
                .item(item)
                .actorUser(actorUser)
                .fromStatus(oldStatus)
                .toStatus(ItemStatus.PENDING)
                .comment(comment != null ? comment : "Submitted for approval")
                .build();
        approvalRepository.save(event);

        log.info("Item {} submitted for approval by {}", item.getCode(), actorUser.getEmail());
        
        // Publish event for async processing
        eventPublisher.publishEvent(new ItemStatusChangedEvent(this, updated, oldStatus));
        
        return updated;
    }

    @Transactional
    public Item approve(UUID itemId, UUID tenantId, UUID actorUserId, String comment) {
        Item item = itemRepository.findByIdAndTenant_Id(itemId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Item", itemId));

        User actorUser = userRepository.findById(actorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", actorUserId));

        // Check if user has permission to approve (MANAGER or ADMIN)
        if (actorUser.getRole() != Role.MANAGER && actorUser.getRole() != Role.ADMIN) {
            throw new ForbiddenException("items", "approve");
        }

        if (item.getStatus() != ItemStatus.PENDING) {
            throw new BusinessValidationException("Item can only be approved from PENDING status");
        }

        ItemStatus oldStatus = item.getStatus();
        item.setStatus(ItemStatus.APPROVED);
        Item updated = itemRepository.save(item);

        // Create approval event
        ApprovalEvent event = ApprovalEvent.builder()
                .item(item)
                .actorUser(actorUser)
                .fromStatus(oldStatus)
                .toStatus(ItemStatus.APPROVED)
                .comment(comment != null ? comment : "Approved")
                .build();
        approvalRepository.save(event);

        log.info("Item {} approved by {}", item.getCode(), actorUser.getEmail());
        
        // Publish events for async processing
        eventPublisher.publishEvent(new ItemStatusChangedEvent(this, updated, oldStatus));
        eventPublisher.publishEvent(new ItemApprovalEvent(this, item.getId(), item.getCode(),
                item.getTenant().getId(), ItemStatus.APPROVED, actorUser.getId(),
                actorUser.getEmail(), comment));
        
        return updated;
    }

    @Transactional
    public Item reject(UUID itemId, UUID tenantId, UUID actorUserId, String comment) {
        Item item = itemRepository.findByIdAndTenant_Id(itemId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Item", itemId));

        User actorUser = userRepository.findById(actorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", actorUserId));

        // Check if user has permission to reject (MANAGER or ADMIN)
        if (actorUser.getRole() != Role.MANAGER && actorUser.getRole() != Role.ADMIN) {
            throw new ForbiddenException("items", "reject");
        }

        if (item.getStatus() != ItemStatus.PENDING) {
            throw new BusinessValidationException("Item can only be rejected from PENDING status");
        }

        if (comment == null || comment.trim().isEmpty()) {
            throw new BusinessValidationException("Rejection comment is required");
        }

        ItemStatus oldStatus = item.getStatus();
        item.setStatus(ItemStatus.REJECTED);
        Item updated = itemRepository.save(item);

        // Create approval event
        ApprovalEvent event = ApprovalEvent.builder()
                .item(item)
                .actorUser(actorUser)
                .fromStatus(oldStatus)
                .toStatus(ItemStatus.REJECTED)
                .comment(comment)
                .build();
        approvalRepository.save(event);

        log.info("Item {} rejected by {} with comment: {}", item.getCode(), actorUser.getEmail(), comment);
        
        // Publish events for async processing
        eventPublisher.publishEvent(new ItemStatusChangedEvent(this, updated, oldStatus));
        eventPublisher.publishEvent(new ItemApprovalEvent(this, item.getId(), item.getCode(),
                item.getTenant().getId(), ItemStatus.REJECTED, actorUser.getId(),
                actorUser.getEmail(), comment));
        
        return updated;
    }

    @Transactional
    public Item close(UUID itemId, UUID tenantId, UUID actorUserId, String comment) {
        Item item = itemRepository.findByIdAndTenant_Id(itemId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Item", itemId));

        User actorUser = userRepository.findById(actorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", actorUserId));

        if (item.getStatus() != ItemStatus.APPROVED) {
            throw new BusinessValidationException("Only approved items can be closed");
        }

        ItemStatus oldStatus = item.getStatus();
        item.setStatus(ItemStatus.CLOSED);
        Item updated = itemRepository.save(item);

        // Create approval event
        ApprovalEvent event = ApprovalEvent.builder()
                .item(item)
                .actorUser(actorUser)
                .fromStatus(oldStatus)
                .toStatus(ItemStatus.CLOSED)
                .comment(comment != null ? comment : "Closed")
                .build();
        approvalRepository.save(event);

        log.info("Item {} closed by {}", item.getCode(), actorUser.getEmail());
        
        // Publish event for async processing
        eventPublisher.publishEvent(new ItemStatusChangedEvent(this, updated, oldStatus));
        
        return updated;
    }

    @Transactional(readOnly = true)
    public List<ApprovalEvent> getItemHistory(UUID itemId) {
        return approvalRepository.findByItem_IdOrderByCreatedAtDesc(itemId);
    }
}
