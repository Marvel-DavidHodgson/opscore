package com.opscore.user;

import com.opscore.auth.JwtAuthenticationFilter;
import com.opscore.user.dto.CreateUserRequest;
import com.opscore.user.dto.UpdateUserRequest;
import com.opscore.user.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Users", description = "User management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "List users", description = "Get all users in the tenant (ADMIN/MANAGER)")
    public ResponseEntity<List<UserDto>> listUsers(Authentication authentication) {
        JwtAuthenticationFilter.UserPrincipal principal = 
                (JwtAuthenticationFilter.UserPrincipal) authentication.getPrincipal();
        
        List<User> users = userService.getUsersByTenantId(principal.tenantId());
        List<UserDto> userDtos = users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(userDtos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Get a specific user by their ID")
    public ResponseEntity<UserDto> getUserById(@PathVariable UUID id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create user", description = "Create a new user in the tenant (ADMIN/MANAGER)")
    public ResponseEntity<UserDto> createUser(
            Authentication authentication,
            @Valid @RequestBody CreateUserRequest request) {
        
        JwtAuthenticationFilter.UserPrincipal principal = 
                (JwtAuthenticationFilter.UserPrincipal) authentication.getPrincipal();
        
        User user = userService.createUser(
                principal.tenantId(),
                request.email(),
                request.password(),
                request.firstName(),
                request.lastName(),
                request.role()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDto(user));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update user", description = "Update user details (ADMIN/MANAGER)")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request) {
        
        User user = userService.updateUser(
                id,
                request.firstName(),
                request.lastName(),
                request.role(),
                request.isActive()
        );
        
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Deactivate user", description = "Deactivate a user (ADMIN/MANAGER)")
    public ResponseEntity<Void> deactivateUser(@PathVariable UUID id) {
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }
}
