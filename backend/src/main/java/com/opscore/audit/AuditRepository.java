package com.opscore.audit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface AuditRepository extends JpaRepository<AuditLog, UUID> {
    
    Page<AuditLog> findByTenant_IdOrderByCreatedAtDesc(UUID tenantId, Pageable pageable);
    
    List<AuditLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(String entityType, UUID entityId);
    
    Page<AuditLog> findByActorUser_IdOrderByCreatedAtDesc(UUID actorUserId, Pageable pageable);
    
    @Query("SELECT a FROM AuditLog a WHERE a.tenant.id = :tenantId " +
           "AND (:entityType IS NULL OR a.entityType = :entityType) " +
           "AND (:actorUserId IS NULL OR a.actorUser.id = :actorUserId) " +
           "AND (a.createdAt BETWEEN :startDate AND :endDate) " +
           "ORDER BY a.createdAt DESC")
    Page<AuditLog> findByFilters(
            @Param("tenantId") UUID tenantId,
            @Param("entityType") String entityType,
            @Param("actorUserId") UUID actorUserId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            Pageable pageable);
    
    // Methods for scheduled maintenance tasks
    long countByCreatedAtBefore(Instant cutoffDate);
    long countByCreatedAtAfter(Instant startDate);
    void deleteByCreatedAtBefore(Instant cutoffDate);
}
