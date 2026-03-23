package com.ecommerce.project.controller;

import com.ecommerce.project.dto.BespokeOrderRequestDTO;
import com.ecommerce.project.dto.BespokeOrderResponseDTO;
import com.ecommerce.project.entity.BespokeOrder;
import com.ecommerce.project.service.BespokeOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bespoke-orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Bespoke Orders", description = "Made-to-measure and bespoke order management")
public class BespokeOrderController {
    
    private final BespokeOrderService bespokeOrderService;
    
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create a bespoke order", description = "Create a new made-to-measure/bespoke order")
    public ResponseEntity<BespokeOrderResponseDTO> createOrder(
            @Valid @RequestBody BespokeOrderRequestDTO dto,
            Authentication authentication) {
        
        String userId = authentication.getName(); // Get user ID from authentication
        BespokeOrderResponseDTO response = bespokeOrderService.createBespokeOrder(userId, dto);
        return ResponseEntity.status(201).body(response);
    }
    
    @PutMapping("/{orderId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update a bespoke order", description = "Update an existing bespoke order")
    public ResponseEntity<BespokeOrderResponseDTO> updateOrder(
            @PathVariable String orderId,
            @Valid @RequestBody BespokeOrderRequestDTO dto) {
        
        BespokeOrderResponseDTO response = bespokeOrderService.updateBespokeOrder(orderId, dto);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update order status", description = "Update the status of a bespoke order (Admin only)")
    public ResponseEntity<BespokeOrderResponseDTO> updateStatus(
            @PathVariable String orderId,
            @RequestParam BespokeOrder.OrderStatus status) {
        
        BespokeOrderResponseDTO response = bespokeOrderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{orderId}/tracking")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Add sample tracking ID", description = "Add tracking ID for sample shirt shipment")
    public ResponseEntity<BespokeOrderResponseDTO> addTrackingId(
            @PathVariable String orderId,
            @RequestParam String trackingId) {
        
        BespokeOrderResponseDTO response = bespokeOrderService.addSampleTrackingId(orderId, trackingId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{orderId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get bespoke order", description = "Get details of a specific bespoke order")
    public ResponseEntity<BespokeOrderResponseDTO> getOrder(@PathVariable String orderId) {
        BespokeOrderResponseDTO response = bespokeOrderService.getBespokeOrder(orderId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/my-orders")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get user's bespoke orders", description = "Get all bespoke orders for the authenticated user")
    public ResponseEntity<List<BespokeOrderResponseDTO>> getMyOrders(Authentication authentication) {
        String userId = authentication.getName();
        List<BespokeOrderResponseDTO> orders = bespokeOrderService.getUserBespokeOrders(userId);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all bespoke orders", description = "Get all bespoke orders with pagination (Admin only)")
    public ResponseEntity<Page<BespokeOrderResponseDTO>> getAllOrders(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) BespokeOrder.OrderStatus status,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") String sortDirection) {
        
        if (status != null) {
            List<BespokeOrderResponseDTO> orders = bespokeOrderService.getBespokeOrdersByStatus(status);
            return ResponseEntity.ok(Page.empty()); // Convert list to page if needed
        }
        
        Sort.Direction direction = sortDirection.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 10, Sort.by(direction, sortBy));
        
        Page<BespokeOrderResponseDTO> orders = bespokeOrderService.getAllBespokeOrders(pageable);
        return ResponseEntity.ok(orders);
    }
    
    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete bespoke order", description = "Delete a bespoke order (Admin only)")
    public ResponseEntity<Void> deleteOrder(@PathVariable String orderId) {
        bespokeOrderService.deleteBespokeOrder(orderId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/shipping-label")
    @Operation(summary = "Get shipping label", description = "Get the shipping address for sending sample shirts")
    public ResponseEntity<Map<String, String>> getShippingLabel() {
        String address = bespokeOrderService.generateShippingLabel();
        
        Map<String, String> response = new HashMap<>();
        response.put("shippingAddress", address);
        response.put("instructions", "Please print this address and attach it to your sample shirt package");
        
        return ResponseEntity.ok(response);
    }
}
