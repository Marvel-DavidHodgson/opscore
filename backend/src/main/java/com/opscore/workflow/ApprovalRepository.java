package com.opscore.workflow;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ApprovalRepository extends JpaRepository<ApprovalEvent, UUID> {
    List<ApprovalEvent> findByItem_IdOrderByCreatedAtDesc(UUID itemId);
    List<ApprovalEvent> findByActorUser_IdOrderByCreatedAtDesc(UUID actorUserId);
    List<ApprovalEvent> findByItem_Tenant_IdOrderByCreatedAtDesc(UUID tenantId);
}
