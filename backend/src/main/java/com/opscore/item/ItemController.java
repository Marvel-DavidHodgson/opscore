package com.opscore.item;

import com.opscore.auth.JwtAuthenticationFilter;
import com.opscore.item.dto.CreateItemRequest;
import com.opscore.item.dto.ItemDto;
import com.opscore.item.dto.UpdateItemRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Items", description = "Item/entity management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class ItemController {

    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @GetMapping
    @Operation(summary = "List items", description = "Get paginated list of items with optional filters")
    public ResponseEntity<Page<ItemDto>> listItems(
            Authentication authentication,
            @RequestParam(required = false) ItemStatus status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) UUID assignedToUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        
        JwtAuthenticationFilter.UserPrincipal principal = 
                (JwtAuthenticationFilter.UserPrincipal) authentication.getPrincipal();
        
        Sort sort = Sort.by(sortDir.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Item> items = itemService.getItemsWithFilters(
                principal.tenantId(),
                status,
                category,
                assignedToUserId,
                pageable
        );
        
        return ResponseEntity.ok(items.map(itemMapper::toDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get item by ID", description = "Get a specific item by its ID")
    public ResponseEntity<ItemDto> getItemById(
            Authentication authentication,
            @PathVariable UUID id) {
        
        JwtAuthenticationFilter.UserPrincipal principal = 
                (JwtAuthenticationFilter.UserPrincipal) authentication.getPrincipal();
        
        Item item = itemService.getItemById(id, principal.tenantId());
        return ResponseEntity.ok(itemMapper.toDto(item));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OPERATOR', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Create item", description = "Create a new item (OPERATOR+)")
    public ResponseEntity<ItemDto> createItem(
            Authentication authentication,
            @Valid @RequestBody CreateItemRequest request) {
        
        JwtAuthenticationFilter.UserPrincipal principal = 
                (JwtAuthenticationFilter.UserPrincipal) authentication.getPrincipal();
        
        Item item = itemService.createItem(
                principal.tenantId(),
                principal.userId(),
                request.title(),
                request.description(),
                request.category(),
                request.assignedToUserId(),
                request.metadata()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(itemMapper.toDto(item));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OPERATOR', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Update item", description = "Update an item (OPERATOR can edit own, MANAGER+ can edit any)")
    public ResponseEntity<ItemDto> updateItem(
            Authentication authentication,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateItemRequest request) {
        
        JwtAuthenticationFilter.UserPrincipal principal = 
                (JwtAuthenticationFilter.UserPrincipal) authentication.getPrincipal();
        
        Item item = itemService.updateItem(
                id,
                principal.tenantId(),
                request.title(),
                request.description(),
                request.category(),
                request.assignedToUserId(),
                request.metadata()
        );
        
        return ResponseEntity.ok(itemMapper.toDto(item));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Operation(summary = "Delete item", description = "Delete an item (MANAGER+ only)")
    public ResponseEntity<Void> deleteItem(
            Authentication authentication,
            @PathVariable UUID id) {
        
        JwtAuthenticationFilter.UserPrincipal principal = 
                (JwtAuthenticationFilter.UserPrincipal) authentication.getPrincipal();
        
        itemService.deleteItem(id, principal.tenantId());
        return ResponseEntity.noContent().build();
    }
}
