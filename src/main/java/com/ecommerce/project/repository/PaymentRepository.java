package com.ecommerce.project.repository;

import com.ecommerce.project.entity.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {

    Optional<Payment> findByOrderId(String orderId);

    Optional<Payment> findByTransactionId(String transactionId);

    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);

    // Find all order IDs that have successful payments
    @Query(value = "{ 'paymentStatus': 'completed' }", fields = "{ 'orderId': 1 }")
    List<Payment> findCompletedPayments();
}
