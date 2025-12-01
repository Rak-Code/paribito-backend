package com.ecommerce.project.controller;

import com.ecommerce.project.dto.PaymentRequestDTO;
import com.ecommerce.project.dto.RazorpayOrderRequestDTO;
import com.ecommerce.project.dto.RazorpayOrderResponseDTO;
import com.ecommerce.project.dto.RazorpayPaymentVerificationDTO;
import com.ecommerce.project.entity.Payment;
import com.ecommerce.project.service.PaymentService;
import com.ecommerce.project.service.RazorpayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final RazorpayService razorpayService;

    @PostMapping
    public ResponseEntity<Payment> process(@RequestBody PaymentRequestDTO dto) {
        Payment p = paymentService.processPayment(dto);
        return ResponseEntity.status(201).body(p);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Payment> getByOrder(@PathVariable String orderId) {
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<Payment> getByPaymentId(@PathVariable String paymentId) {
        return ResponseEntity.ok(paymentService.getPayment(paymentId));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public java.util.List<Payment> allPayments() {
        return paymentService.getAllPayments();
    }

    // Razorpay Integration Endpoints

    @PostMapping("/razorpay/create-order")
    public ResponseEntity<RazorpayOrderResponseDTO> createRazorpayOrder(@RequestBody RazorpayOrderRequestDTO dto) {
        RazorpayOrderResponseDTO response = razorpayService.createRazorpayOrder(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/razorpay/verify")
    public ResponseEntity<Payment> verifyRazorpayPayment(@RequestBody RazorpayPaymentVerificationDTO dto) {
        Payment payment = razorpayService.verifyPayment(dto);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/razorpay/refund/{paymentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> refundRazorpayPayment(
            @PathVariable String paymentId,
            @RequestParam double amount) {
        String refundId = razorpayService.refundPayment(paymentId, amount);
        return ResponseEntity.ok("Refund successful. Refund ID: " + refundId);
    }
}
