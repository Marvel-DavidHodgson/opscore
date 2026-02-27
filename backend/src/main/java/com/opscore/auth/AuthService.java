package com.opscore.auth;

import com.opscore.auth.dto.LoginRequest;
import com.opscore.auth.dto.LoginResponse;
import com.opscore.auth.dto.RefreshTokenRequest;
import com.opscore.config.JwtConfig;
import com.opscore.user.User;
import com.opscore.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final JwtConfig jwtConfig;

    @Transactional
    public LoginResponse login(LoginRequest request, UUID tenantId) {
        User user = userRepository.findByEmailAndTenant_Id(request.username(), tenantId)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!user.getIsActive()) {
            throw new RuntimeException("User account is deactivated");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid username or password");
        }

        // Update last login
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        // Generate tokens
        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId(),
                user.getTenant().getId(),
                user.getEmail(),
                user.getRole()
        );

        String refreshTokenStr = jwtTokenProvider.generateRefreshToken(user.getId());

        // Save refresh token to database
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(refreshTokenStr)
                .expiresAt(Instant.now().plusMillis(jwtConfig.getRefreshTokenExpiry()))
                .build();
        refreshTokenRepository.save(refreshToken);

        log.info("User {} logged in successfully", user.getEmail());

        return new LoginResponse(
                accessToken,
                refreshTokenStr,
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.getTenant().getId(),
                user.getTenant().getName(),
                jwtConfig.getAccessTokenExpiry()
        );
    }

    @Transactional
    public LoginResponse refreshAccessToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (!refreshToken.isValid()) {
            throw new RuntimeException("Refresh token is invalid or expired");
        }

        User user = refreshToken.getUser();

        if (!user.getIsActive()) {
            throw new RuntimeException("User account is deactivated");
        }

        // Generate new access token
        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId(),
                user.getTenant().getId(),
                user.getEmail(),
                user.getRole()
        );

        log.info("Access token refreshed for user {}", user.getEmail());

        return new LoginResponse(
                accessToken,
                request.refreshToken(),
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.getTenant().getId(),
                user.getTenant().getName(),
                jwtConfig.getAccessTokenExpiry()
        );
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                    log.info("User logged out, refresh token revoked");
                });
    }

    @Transactional
    public void logoutAllSessions(UUID userId) {
        var tokens = refreshTokenRepository.findByUser_IdAndRevokedFalse(userId);
        tokens.forEach(token -> token.setRevoked(true));
        refreshTokenRepository.saveAll(tokens);
        log.info("All sessions logged out for user {}", userId);
    }
}
