package com.ecommerce.project.service;

import com.ecommerce.project.entity.Invoice;
import com.ecommerce.project.entity.Order;
import com.ecommerce.project.entity.Payment;
import com.ecommerce.project.entity.Product;
import com.ecommerce.project.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnMissingBean(EmailService.class)
public class NoopEmailService implements EmailService {

    @Override
    @Async("emailExecutor")
    public void sendPaymentSuccessEmail(Payment payment, Order order, User user) {
        log.warn("EmailService not configured. Skipping payment success email for order: {}", order != null ? order.getId() : null);
    }

    @Override
    @Async("emailExecutor")
    public void sendOrderConfirmationToCustomer(Order order, User user) {
        log.warn("EmailService not configured. Skipping order confirmation email for order: {}", order != null ? order.getId() : null);
    }

    @Override
    @Async("emailExecutor")
    public void sendOrderNotificationToAdmin(Order order, User user) {
        log.warn("EmailService not configured. Skipping admin order notification for order: {}", order != null ? order.getId() : null);
    }

    @Override
    @Async("emailExecutor")
    public void sendOrderCancellationToCustomer(Order order, User user) {
        log.warn("EmailService not configured. Skipping order cancellation email for order: {}", order != null ? order.getId() : null);
    }

    @Override
    @Async("emailExecutor")
    public void sendOrderCancellationToAdmin(Order order, User user) {
        log.warn("EmailService not configured. Skipping admin cancellation notification for order: {}", order != null ? order.getId() : null);
    }

    @Override
    @Async("emailExecutor")
    public void sendCartReminderEmail(User user, Product product) {
        log.warn("EmailService not configured. Skipping cart reminder email for user: {}", user != null ? user.getId() : null);
    }

    @Override
    @Async("emailExecutor")
    public void sendWishlistReminderEmail(User user, Product product) {
        log.warn("EmailService not configured. Skipping wishlist reminder email for user: {}", user != null ? user.getId() : null);
    }

    @Override
    @Async("emailExecutor")
    public void sendInvoiceToCustomer(Invoice invoice, User user, byte[] pdfData) {
        log.warn("EmailService not configured. Skipping invoice email to customer for invoice: {}", invoice != null ? invoice.getInvoiceNumber() : null);
    }

    @Override
    @Async("emailExecutor")
    public void sendInvoiceToAdmin(Invoice invoice, User user, byte[] pdfData) {
        log.warn("EmailService not configured. Skipping invoice email to admin for invoice: {}", invoice != null ? invoice.getInvoiceNumber() : null);
    }
}
