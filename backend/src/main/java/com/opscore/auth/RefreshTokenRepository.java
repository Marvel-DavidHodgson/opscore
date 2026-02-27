package com.opscore.auth;

import com.opscore.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);
    List<RefreshToken> findByUser(User user);
    void deleteByUser(User user);
    void deleteByExpiresAtBefore(Instant now);
    List<RefreshToken> findByUser_IdAndRevokedFalse(UUID userId);
}
