package com.opscore.report;

import com.opscore.item.Item;
import com.opscore.item.ItemRepository;
import com.opscore.item.ItemStatus;
import com.opscore.report.dto.ItemStatusBreakdownDto;
import com.opscore.report.dto.KpiSummaryDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ItemRepository itemRepository;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public KpiSummaryDto getKpiSummary(UUID tenantId) {
        // Total items
        Long totalItems = entityManager.createQuery(
                        "SELECT COUNT(i) FROM Item i WHERE i.tenant.id = :tenantId",
                        Long.class)
                .setParameter("tenantId", tenantId)
                .getSingleResult();

        // Items by status
        List<Tuple> statusResults = entityManager.createQuery(
                        "SELECT i.status as status, COUNT(i) as count FROM Item i " +
                                "WHERE i.tenant.id = :tenantId GROUP BY i.status",
                        Tuple.class)
                .setParameter("tenantId", tenantId)
                .getResultList();

        Map<String, Long> itemsByStatus = statusResults.stream()
                .collect(Collectors.toMap(
                        t -> t.get("status", ItemStatus.class).name(),
                        t -> t.get("count", Long.class)
                ));

        List<ItemStatusBreakdownDto> statusBreakdown = statusResults.stream()
                .map(t -> new ItemStatusBreakdownDto(
                        t.get("status", ItemStatus.class),
                        t.get("count", Long.class)
                ))
                .collect(Collectors.toList());

        // Items by assignee
        List<Tuple> assigneeResults = entityManager.createQuery(
                        "SELECT CONCAT(u.firstName, ' ', u.lastName) as assigneeName, COUNT(i) as count " +
                                "FROM Item i JOIN i.assignedToUser u " +
                                "WHERE i.tenant.id = :tenantId " +
                                "GROUP BY u.id, u.firstName, u.lastName",
                        Tuple.class)
                .setParameter("tenantId", tenantId)
                .getResultList();

        Map<String, Long> itemsByAssignee = assigneeResults.stream()
                .collect(Collectors.toMap(
                        t -> t.get("assigneeName", String.class),
                        t -> t.get("count", Long.class)
                ));

        log.info("Generated KPI summary for tenant {}", tenantId);

        return new KpiSummaryDto(
                totalItems,
                itemsByStatus,
                itemsByAssignee,
                statusBreakdown
        );
    }

    @Transactional(readOnly = true)
    public List<Item> getItemsForExport(UUID tenantId, ItemStatus status, String category) {
        String jpql = "SELECT i FROM Item i WHERE i.tenant.id = :tenantId";

        if (status != null) {
            jpql += " AND i.status = :status";
        }

        if (category != null) {
            jpql += " AND i.category = :category";
        }

        jpql += " ORDER BY i.createdAt DESC";

        var query = entityManager.createQuery(jpql, Item.class)
                .setParameter("tenantId", tenantId);

        if (status != null) {
            query.setParameter("status", status);
        }

        if (category != null) {
            query.setParameter("category", category);
        }

        return query.getResultList();
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getItemCountsByCategory(UUID tenantId) {
        List<Tuple> results = entityManager.createQuery(
                        "SELECT i.category as category, COUNT(i) as count FROM Item i " +
                                "WHERE i.tenant.id = :tenantId AND i.category IS NOT NULL " +
                                "GROUP BY i.category ORDER BY COUNT(i) DESC",
                        Tuple.class)
                .setParameter("tenantId", tenantId)
                .getResultList();

        return results.stream()
                .collect(Collectors.toMap(
                        t -> t.get("category", String.class),
                        t -> t.get("count", Long.class)
                ));
    }
}
