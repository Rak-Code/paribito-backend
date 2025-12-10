package com.ecommerce.project.service;

import com.ecommerce.project.dto.RazorpayOrderRequestDTO;
import com.ecommerce.project.dto.RazorpayOrderResponseDTO;
import com.ecommerce.project.dto.RazorpayPaymentVerificationDTO;
import com.ecommerce.project.entity.Payment;
import com.ecommerce.project.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RazorpayServiceImpl implements RazorpayService {

    private final RestClient razorpayRestClient;
    private final PaymentRepository paymentRepository;

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    @Value("${razorpay.currency}")
    private String currency;

    @Override
    public RazorpayOrderResponseDTO createRazorpayOrder(RazorpayOrderRequestDTO dto) {
        try {
            Map<String, Object> orderRequest = Map.of(
                    "amount", (int) (dto.amount() * 100), // Amount in paise
                    "currency", currency,
                    "receipt", dto.orderId()
            );

            Map<String, Object> order = razorpayRestClient.post()
                    .uri("/orders")
                    .body(orderRequest)
                    .retrieve()
                    .body(Map.class);

            // Create payment record with pending status
            Payment payment = new Payment();
            payment.setOrderId(dto.orderId());
            payment.setAmount(dto.amount());
            payment.setPaymentMethod(Payment.PaymentMethod.razorpay);
            payment.setPaymentStatus(Payment.PaymentStatus.pending);
            payment.setRazorpayOrderId((String) order.get("id"));
            paymentRepository.save(payment);

            return new RazorpayOrderResponseDTO(
                    (String) order.get("id"),
                    dto.orderId(),
                    dto.amount(),
                    currency,
                    keyId
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to create Razorpay order: " + e.getMessage(), e);
        }
    }

    @Override
    public Payment verifyPayment(RazorpayPaymentVerificationDTO dto) {
        try {
            // Verify signature using HMAC SHA256
            String payload = dto.razorpayOrderId() + "|" + dto.razorpayPaymentId();
            String generatedSignature = generateSignature(payload, keySecret);

            if (!generatedSignature.equals(dto.razorpaySignature())) {
                throw new RuntimeException("Invalid payment signature");
            }

            // Update payment record
            Payment payment = paymentRepository.findByRazorpayOrderId(dto.razorpayOrderId())
                    .orElseThrow(() -> new RuntimeException("Payment not found"));

            payment.setRazorpayPaymentId(dto.razorpayPaymentId());
            payment.setRazorpaySignature(dto.razorpaySignature());
            payment.setPaymentStatus(Payment.PaymentStatus.completed);
            payment.setTransactionId(dto.razorpayPaymentId());
            payment.setPaymentDate(LocalDateTime.now());

            return paymentRepository.save(payment);

        } catch (Exception e) {
            throw new RuntimeException("Payment verification failed: " + e.getMessage(), e);
        }
    }

    private String generateSignature(String payload, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate signature", e);
        }
    }

    @Override
    public String refundPayment(String paymentId, double amount) {
        try {
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new RuntimeException("Payment not found"));

            if (payment.getRazorpayPaymentId() == null) {
                throw new RuntimeException("Razorpay payment ID not found");
            }

            Map<String, Object> refundRequest = Map.of(
                    "amount", (int) (amount * 100), // Amount in paise
                    "speed", "normal"
            );

            // Create refund using RestClient
            Map<String, Object> refund = razorpayRestClient.post()
                    .uri("/payments/{paymentId}/refund", payment.getRazorpayPaymentId())
                    .body(refundRequest)
                    .retrieve()
                    .body(Map.class);

            // Update payment status
            payment.setPaymentStatus(Payment.PaymentStatus.refunded);
            paymentRepository.save(payment);

            return (String) refund.get("id");

        } catch (Exception e) {
            throw new RuntimeException("Refund failed: " + e.getMessage(), e);
        }
    }
}
