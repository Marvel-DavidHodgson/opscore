package com.opscore.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opscore.auth.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditAspect {

    private final AuditRepository auditRepository;
    private final ObjectMapper objectMapper;

    @Around("execution(public * com.opscore..service.*Service.create*(..)) || " +
            "execution(public * com.opscore..service.*Service.update*(..)) || " +
            "execution(public * com.opscore..service.*Service.delete*(..))")
    public Object auditServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getName();
        String className = signature.getDeclaringType().getSimpleName();

        // Extract user and tenant from security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID actorUserId = null;
        UUID tenantId = null;

        if (authentication != null && authentication.getPrincipal() instanceof JwtAuthenticationFilter.UserPrincipal principal) {
            actorUserId = principal.userId();
            tenantId = principal.tenantId();
        }

        // Get HTTP request details
        String ipAddress = null;
        String userAgent = null;
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            ipAddress = getClientIpAddress(request);
            userAgent = request.getHeader("User-Agent");
        }

        // Capture arguments before method execution
        Map<String, Object> oldValue = captureArguments(joinPoint);

        // Execute the method
        Object result = joinPoint.proceed();

        // Capture result after method execution
        Map<String, Object> newValue = null;
        if (result != null) {
            newValue = new HashMap<>();
            newValue.put("result", serializeObject(result));
        }

        // Determine action and entity
        String action = methodName.toUpperCase();
        String entityType = className.replace("Service", "");
        UUID entityId = extractEntityId(result, joinPoint.getArgs());

        // Save audit log (only if we have tenant context)
        if (tenantId != null) {
            try {
                AuditLog auditLog = AuditLog.builder()
                        .tenant(new com.opscore.tenant.Tenant())
                        .actorUser(actorUserId != null ? new com.opscore.user.User() : null)
                        .entityType(entityType)
                        .entityId(entityId != null ? entityId : UUID.randomUUID())
                        .action(action)
                        .oldValue(oldValue)
                        .newValue(newValue)
                        .ipAddress(ipAddress)
                        .userAgent(userAgent)
                        .build();

                // Set tenant and user IDs directly
                auditLog.getTenant().setId(tenantId);
                if (actorUserId != null && auditLog.getActorUser() != null) {
                    auditLog.getActorUser().setId(actorUserId);
                }

                auditRepository.save(auditLog);
                log.debug("Audit log created for {} action on {} by user {}", action, entityType, actorUserId);
            } catch (Exception e) {
                log.error("Failed to create audit log", e);
            }
        }

        return result;
    }

    private Map<String, Object> captureArguments(ProceedingJoinPoint joinPoint) {
        Map<String, Object> args = new HashMap<>();
        Object[] argValues = joinPoint.getArgs();
        String[] argNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();

        for (int i = 0; i < argValues.length; i++) {
            if (argValues[i] != null) {
                args.put(argNames[i], serializeObject(argValues[i]));
            }
        }

        return args;
    }

    private Object serializeObject(Object obj) {
        try {
            // Avoid serializing large objects
            if (obj instanceof String || obj instanceof Number || obj instanceof Boolean || obj instanceof UUID) {
                return obj;
            }
            return obj.getClass().getSimpleName() + "@" + Integer.toHexString(obj.hashCode());
        } catch (Exception e) {
            return obj.toString();
        }
    }

    private UUID extractEntityId(Object result, Object[] args) {
        // Try to extract UUID from result
        if (result != null) {
            try {
                var method = result.getClass().getMethod("getId");
                Object id = method.invoke(result);
                if (id instanceof UUID) {
                    return (UUID) id;
                }
            } catch (Exception ignored) {
            }
        }

        // Try to find UUID in arguments
        for (Object arg : args) {
            if (arg instanceof UUID) {
                return (UUID) arg;
            }
        }

        return null;
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP"};

        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr();
    }
}
