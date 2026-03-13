package com.ecommerce.project.service;

import com.ecommerce.project.entity.Invoice;
import com.ecommerce.project.entity.Order;
import com.ecommerce.project.entity.Payment;
import com.ecommerce.project.entity.Product;
import com.ecommerce.project.entity.User;

public interface EmailService {
    
    // Payment and Order Emails (triggered after payment verification)
    
    /**
     * Sends a payment success confirmation email to the customer.
     * Should be called after Razorpay payment is successfully verified.
     * 
     * @param payment the verified payment details
     * @param order the associated order
     * @param user the customer who made the payment
     */
    void sendPaymentSuccessEmail(Payment payment, Order order, User user);
    
    void sendOrderConfirmationToCustomer(Order order, User user);
    
    void sendOrderNotificationToAdmin(Order order, User user);
    
    // Cancellation Emails
    
    /**
     * Sends an order cancellation notification email to the customer.
     * Should be called when an order status changes to cancelled.
     * 
     * @param order the cancelled order
     * @param user the customer whose order was cancelled
     */
    void sendOrderCancellationToCustomer(Order order, User user);
    
    /**
     * Sends an order cancellation notification email to the admin.
     * Should be called when an order status changes to cancelled.
     * 
     * @param order the cancelled order
     * @param user the customer whose order was cancelled
     */
    void sendOrderCancellationToAdmin(Order order, User user);

    // Reminder Emails
    
    void sendCartReminderEmail(User user, Product product);

    void sendWishlistReminderEmail(User user, Product product);
    
    // Invoice Emails
    
    void sendInvoiceToCustomer(Invoice invoice, User user, byte[] pdfData);
    
    void sendInvoiceToAdmin(Invoice invoice, User user, byte[] pdfData);
    
    // Password Reset Emails
    
    /**
     * Sends an OTP email for password reset.
     * Should be called when user requests password reset.
     * 
     * @param toEmail the email address to send OTP to
     * @param otpCode the 6-digit OTP code
     */
    void sendOtpEmail(String toEmail, String otpCode);
}
