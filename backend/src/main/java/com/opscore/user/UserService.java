package com.opscore.user;

import com.opscore.tenant.Tenant;
import com.opscore.tenant.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional(readOnly = true)
    public List<User> getUsersByTenantId(UUID tenantId) {
        return userRepository.findByTenant_Id(tenantId);
    }

    @Transactional(readOnly = true)
    public List<User> getActiveUsersByTenantId(UUID tenantId) {
        return userRepository.findByTenant_IdAndIsActive(tenantId, true);
    }

    @Transactional
    public User createUser(UUID tenantId, String email, String password, String firstName, String lastName, Role role) {
        if (userRepository.existsByEmailAndTenant_Id(email, tenantId)) {
            throw new RuntimeException("User with email " + email + " already exists in this tenant");
        }

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        User user = User.builder()
                .tenant(tenant)
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .firstName(firstName)
                .lastName(lastName)
                .role(role != null ? role : Role.VIEWER)
                .isActive(true)
                .build();

        User created = userRepository.save(user);
        log.info("User {} created in tenant {}", created.getEmail(), tenant.getName());
        return created;
    }

    @Transactional
    public User updateUser(UUID userId, String firstName, String lastName, Role role, Boolean isActive) {
        User user = getUserById(userId);

        if (firstName != null) {
            user.setFirstName(firstName);
        }

        if (lastName != null) {
            user.setLastName(lastName);
        }

        if (role != null) {
            user.setRole(role);
        }

        if (isActive != null) {
            user.setIsActive(isActive);
        }

        User updated = userRepository.save(user);
        log.info("User {} updated", updated.getEmail());
        return updated;
    }

    @Transactional
    public void deactivateUser(UUID userId) {
        User user = getUserById(userId);
        user.setIsActive(false);
        userRepository.save(user);
        log.info("User {} deactivated", user.getEmail());
    }

    @Transactional
    public void changePassword(UUID userId, String oldPassword, String newPassword) {
        User user = getUserById(userId);

        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new RuntimeException("Old password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password changed for user {}", user.getEmail());
    }
}
