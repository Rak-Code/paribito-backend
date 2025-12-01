package com.ecommerce.project.controller;

import com.ecommerce.project.dto.OrderRequestDTO;
import com.ecommerce.project.dto.OrderResponseDTO;
import com.ecommerce.project.entity.Order;
import com.ecommerce.project.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> placeOrder(@RequestBody OrderRequestDTO dto) {
        OrderResponseDTO created = orderService.createOrder(dto);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponseDTO>> userOrders(@PathVariable String userId) {
        return ResponseEntity.ok(orderService.getUserOrders(userId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> get(@PathVariable String orderId) {
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponseDTO> updateStatus(@PathVariable String orderId, @RequestBody Map<String, String> body) {
        Order.Status status = Order.Status.valueOf(body.get("status").toLowerCase());
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status.name()));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponseDTO>> allOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
}
