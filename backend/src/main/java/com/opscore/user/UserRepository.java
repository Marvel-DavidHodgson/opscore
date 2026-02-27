package com.opscore.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmailAndTenant_Id(String email, UUID tenantId);
    List<User> findByTenant_Id(UUID tenantId);
    List<User> findByTenant_IdAndIsActive(UUID tenantId, Boolean isActive);
    boolean existsByEmailAndTenant_Id(String email, UUID tenantId);
}
