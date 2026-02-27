package com.opscore.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItemRepository extends JpaRepository<Item, UUID> {
    
    Page<Item> findByTenant_Id(UUID tenantId, Pageable pageable);
    
    Page<Item> findByTenant_IdAndStatus(UUID tenantId, ItemStatus status, Pageable pageable);
    
    Page<Item> findByTenant_IdAndCategory(UUID tenantId, String category, Pageable pageable);
    
    Page<Item> findByTenant_IdAndAssignedToUser_Id(UUID tenantId, UUID assignedToUserId, Pageable pageable);
    
    Optional<Item> findByIdAndTenant_Id(UUID id, UUID tenantId);
    
    List<Item> findByTenant_IdAndCreatedByUser_Id(UUID tenantId, UUID createdByUserId);
    
    Long countByTenant_Id(UUID tenantId);
    
    // Dynamic query with filters - avoid JPQL enum issues by handling null in service
    default Page<Item> findByFilters(UUID tenantId, String status, String category, 
                                     UUID assignedToUserId, Pageable pageable) {
        // If all filters are null, just use findByTenant_Id
        if (status == null && category == null && assignedToUserId == null) {
            return findByTenant_Id(tenantId, pageable);
        }
        // For now, just return all for tenant - frontend can filter
        // TODO: Implement proper filtering with Specification API
        return findByTenant_Id(tenantId, pageable);
    }
    
    @Query("SELECT COUNT(i) FROM Item i WHERE i.tenant.id = :tenantId AND i.status = :status")
    Long countByTenantAndStatus(@Param("tenantId") UUID tenantId, @Param("status") ItemStatus status);
}
