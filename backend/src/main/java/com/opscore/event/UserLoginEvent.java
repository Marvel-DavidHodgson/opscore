package com.opscore.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

/**
 * Event published when a user logs in successfully.
 * 
 * Use cases:
 * - Update last login timestamp
 * - Log security audit
 * - Track user activity
 * - Send login alerts (if configured)
 */
@Getter
public class UserLoginEvent extends ApplicationEvent {
    
    private final UUID userId;
    private final String email;
    private final UUID tenantId;
    private final String ipAddress;
    private final String userAgent;
    
    public UserLoginEvent(Object source, UUID userId, String email, UUID tenantId,
                         String ipAddress, String userAgent) {
        super(source);
        this.userId = userId;
        this.email = email;
        this.tenantId = tenantId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }
}
