package com.opscore.workflow;

import com.opscore.auth.JwtAuthenticationFilter;
import com.opscore.item.Item;
import com.opscore.item.ItemMapper;
import com.opscore.item.dto.ItemDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/items/{itemId}")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Workflow", description = "Item workflow and approval endpoints")
@SecurityRequirement(name = "bearerAuth")
public class WorkflowController {

    private final WorkflowService workflowService;
    private final ItemMapper itemMapper;

    @PostMapping("/submit")
    @PreAuthorize("hasAnyRole('OPERATOR', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Submit for approval", description = "Submit an item for approval (OPERATOR+)")
    public ResponseEntity<ItemDto> submitForApproval(
            Authentication authentication,
            @PathVariable UUID itemId,
            @Valid @RequestBody(required = false) WorkflowActionRequest request) {
        
        JwtAuthenticationFilter.UserPrincipal principal = 
                (JwtAuthenticationFilter.UserPrincipal) authentication.getPrincipal();
        
        String comment = request != null ? request.comment() : null;
        Item item = workflowService.submitForApproval(
                itemId,
                principal.tenantId(),
                principal.userId(),
                comment
        );
        
        return ResponseEntity.ok(itemMapper.toDto(item));
    }

    @PostMapping("/approve")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Operation(summary = "Approve item", description = "Approve a pending item (MANAGER+)")
    public ResponseEntity<ItemDto> approve(
            Authentication authentication,
            @PathVariable UUID itemId,
            @Valid @RequestBody(required = false) WorkflowActionRequest request) {
        
        JwtAuthenticationFilter.UserPrincipal principal = 
                (JwtAuthenticationFilter.UserPrincipal) authentication.getPrincipal();
        
        String comment = request != null ? request.comment() : null;
        Item item = workflowService.approve(
                itemId,
                principal.tenantId(),
                principal.userId(),
                comment
        );
        
        return ResponseEntity.ok(itemMapper.toDto(item));
    }

    @PostMapping("/reject")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Operation(summary = "Reject item", description = "Reject a pending item (MANAGER+)")
    public ResponseEntity<ItemDto> reject(
            Authentication authentication,
            @PathVariable UUID itemId,
            @Valid @RequestBody WorkflowActionRequest request) {
        
        JwtAuthenticationFilter.UserPrincipal principal = 
                (JwtAuthenticationFilter.UserPrincipal) authentication.getPrincipal();
        
        Item item = workflowService.reject(
                itemId,
                principal.tenantId(),
                principal.userId(),
                request.comment()
        );
        
        return ResponseEntity.ok(itemMapper.toDto(item));
    }

    @PostMapping("/close")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Operation(summary = "Close item", description = "Close an approved item (MANAGER+)")
    public ResponseEntity<ItemDto> close(
            Authentication authentication,
            @PathVariable UUID itemId,
            @Valid @RequestBody(required = false) WorkflowActionRequest request) {
        
        JwtAuthenticationFilter.UserPrincipal principal = 
                (JwtAuthenticationFilter.UserPrincipal) authentication.getPrincipal();
        
        String comment = request != null ? request.comment() : null;
        Item item = workflowService.close(
                itemId,
                principal.tenantId(),
                principal.userId(),
                comment
        );
        
        return ResponseEntity.ok(itemMapper.toDto(item));
    }

    @GetMapping("/history")
    @Operation(summary = "Get item history", description = "Get approval history for an item")
    public ResponseEntity<List<ApprovalEvent>> getItemHistory(@PathVariable UUID itemId) {
        List<ApprovalEvent> history = workflowService.getItemHistory(itemId);
        return ResponseEntity.ok(history);
    }
}
