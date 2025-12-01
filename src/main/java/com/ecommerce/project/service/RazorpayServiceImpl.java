package com.ecommerce.project.service;

import com.ecommerce.project.dto.RazorpayOrderRequestDTO;
import com.ecommerce.project.dto.RazorpayOrderResponseDTO;
import com.ecommerce.project.dto.RazorpayPaymentVerificationDTO;
import com.ecommerce.project.entity.Payment;
import com.ecommerce.project.repository.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RazorpayServiceImpl implements RazorpayService {

    private final RazorpayClient razorpayClient;
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
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", (int) (dto.amount() * 100)); // Amount in paise
            orderRequest.put("currency", currency);
            orderRequest.put("receipt", dto.orderId());

            Order order = razorpayClient.orders.create(orderRequest);

            // Create payment record with pending status
            Payment payment = new Payment();
            payment.setOrderId(dto.orderId());
            payment.setAmount(dto.amount());
            payment.setPaymentMethod(Payment.PaymentMethod.razorpay);
            payment.setPaymentStatus(Payment.PaymentStatus.pending);
            payment.setRazorpayOrderId(order.get("id"));
            paymentRepository.save(payment);

            return new RazorpayOrderResponseDTO(
                    order.get("id"),
                    dto.orderId(),
                    dto.amount(),
                    currency,
                    keyId
            );

        } catch (RazorpayException e) {
            throw new RuntimeException("Failed to create Razorpay order: " + e.getMessage(), e);
        }
    }

    @Override
    public Payment verifyPayment(RazorpayPaymentVerificationDTO dto) {
        try {
            // Verify signature
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", dto.razorpayOrderId());
            options.put("razorpay_payment_id", dto.razorpayPaymentId());
            options.put("razorpay_signature", dto.razorpaySignature());

            boolean isValidSignature = Utils.verifyPaymentSignature(options, keySecret);

            if (!isValidSignature) {
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

        } catch (RazorpayException e) {
            throw new RuntimeException("Payment verification failed: " + e.getMessage(), e);
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

            JSONObject refundRequest = new JSONObject();
            refundRequest.put("amount", (int) (amount * 100)); // Amount in paise
            refundRequest.put("speed", "normal");

            // Create refund using Razorpay client
            com.razorpay.Refund refund = razorpayClient.payments.refund(payment.getRazorpayPaymentId(), refundRequest);

            // Update payment status
            payment.setPaymentStatus(Payment.PaymentStatus.refunded);
            paymentRepository.save(payment);

            return refund.get("id");

        } catch (RazorpayException e) {
            throw new RuntimeException("Refund failed: " + e.getMessage(), e);
        }
    }
}
