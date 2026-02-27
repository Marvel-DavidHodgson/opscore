package com.opscore.workflow;

import com.opscore.item.Item;
import com.opscore.item.ItemRepository;
import com.opscore.item.ItemStatus;
import com.opscore.user.Role;
import com.opscore.user.User;
import com.opscore.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Transactional
    public Item submitForApproval(UUID itemId, UUID tenantId, UUID actorUserId, String comment) {
        Item item = itemRepository.findByIdAndTenant_Id(itemId, tenantId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        User actorUser = userRepository.findById(actorUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (item.getStatus() != ItemStatus.DRAFT && item.getStatus() != ItemStatus.REJECTED) {
            throw new RuntimeException("Item can only be submitted from DRAFT or REJECTED status");
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
        return updated;
    }

    @Transactional
    public Item approve(UUID itemId, UUID tenantId, UUID actorUserId, String comment) {
        Item item = itemRepository.findByIdAndTenant_Id(itemId, tenantId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        User actorUser = userRepository.findById(actorUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user has permission to approve (MANAGER or ADMIN)
        if (actorUser.getRole() != Role.MANAGER && actorUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("User does not have permission to approve");
        }

        if (item.getStatus() != ItemStatus.PENDING) {
            throw new RuntimeException("Item can only be approved from PENDING status");
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
        return updated;
    }

    @Transactional
    public Item reject(UUID itemId, UUID tenantId, UUID actorUserId, String comment) {
        Item item = itemRepository.findByIdAndTenant_Id(itemId, tenantId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        User actorUser = userRepository.findById(actorUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user has permission to reject (MANAGER or ADMIN)
        if (actorUser.getRole() != Role.MANAGER && actorUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("User does not have permission to reject");
        }

        if (item.getStatus() != ItemStatus.PENDING) {
            throw new RuntimeException("Item can only be rejected from PENDING status");
        }

        if (comment == null || comment.trim().isEmpty()) {
            throw new RuntimeException("Rejection comment is required");
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
        return updated;
    }

    @Transactional
    public Item close(UUID itemId, UUID tenantId, UUID actorUserId, String comment) {
        Item item = itemRepository.findByIdAndTenant_Id(itemId, tenantId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        User actorUser = userRepository.findById(actorUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (item.getStatus() != ItemStatus.APPROVED) {
            throw new RuntimeException("Only approved items can be closed");
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
        return updated;
    }

    @Transactional(readOnly = true)
    public List<ApprovalEvent> getItemHistory(UUID itemId) {
        return approvalRepository.findByItem_IdOrderByCreatedAtDesc(itemId);
    }
}
