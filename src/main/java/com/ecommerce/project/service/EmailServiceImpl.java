package com.ecommerce.project.service;

import com.ecommerce.project.entity.Invoice;
import com.ecommerce.project.entity.Order;
import com.ecommerce.project.entity.Payment;
import com.ecommerce.project.entity.Product;
import com.ecommerce.project.entity.User;
import com.ecommerce.project.util.EmailTemplateUtil;
import com.ecommerce.project.util.LogSanitizer;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Implementation of EmailService that sends HTML-formatted emails
 * using templates from EmailTemplateUtil.
 * 
 * All email operations are asynchronous and include comprehensive error handling
 * to ensure email failures do not affect core business operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final EmailTemplateUtil templateUtil;

    @Value("${email.from}")
    private String fromEmail;

    @Value("${email.admin}")
    private String adminEmail;

    /**
     * Validates an email address using a basic regex pattern.
     * @param email the email address to validate
     * @return true if the email is valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }


    /**
     * Sends an HTML email using MimeMessage.
     * Error messages are sanitized to prevent sensitive data exposure.
     * 
     * @param to recipient email address
     * @param subject email subject
     * @param htmlContent HTML content of the email
     * @return true if email was sent successfully, false otherwise
     */
    private boolean sendHtmlEmail(String to, String subject, String htmlContent) {
        if (!isValidEmail(to)) {
            log.warn("Invalid email address, skipping email send: {}", to);
            return false;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true indicates HTML content
            
            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
            return true;
        } catch (MessagingException e) {
            // Sanitize exception to prevent sensitive data exposure (Requirements 10.3)
            log.error("Failed to create email message for: {} - {}", to, LogSanitizer.sanitizeException(e));
            return false;
        } catch (MailException e) {
            // Sanitize SMTP errors to prevent credential exposure (Requirements 10.3)
            log.error("SMTP connection failed while sending email to: {} - {}", to, LogSanitizer.sanitizeException(e));
            return false;
        } catch (Exception e) {
            // Sanitize unexpected errors (Requirements 10.3)
            log.error("Unexpected error while sending email to: {} - {}", to, LogSanitizer.sanitizeException(e));
            return false;
        }
    }

    /**
     * Sends an HTML email with a PDF attachment.
     * Error messages are sanitized to prevent sensitive data exposure.
     * 
     * @param to recipient email address
     * @param subject email subject
     * @param htmlContent HTML content of the email
     * @param attachmentName name of the attachment file
     * @param attachmentData PDF data as byte array
     * @return true if email was sent successfully, false otherwise
     */
    private boolean sendHtmlEmailWithAttachment(String to, String subject, String htmlContent, 
                                                 String attachmentName, byte[] attachmentData) {
        if (!isValidEmail(to)) {
            log.warn("Invalid email address, skipping email send: {}", to);
            return false;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            // Try to add attachment
            try {
                if (attachmentData != null && attachmentData.length > 0) {
                    helper.addAttachment(attachmentName, new ByteArrayResource(attachmentData));
                }
            } catch (MessagingException e) {
                // Sanitize exception to prevent sensitive data exposure (Requirements 10.3)
                log.error("Failed to attach PDF to email, sending without attachment - {}", 
                         LogSanitizer.sanitizeException(e));
                // Continue sending email without attachment
            }
            
            mailSender.send(message);
            log.info("Email with attachment sent successfully to: {}", to);
            return true;
        } catch (MessagingException e) {
            // Sanitize exception to prevent sensitive data exposure (Requirements 10.3)
            log.error("Failed to create email message for: {} - {}", to, LogSanitizer.sanitizeException(e));
            return false;
        } catch (MailException e) {
            // Sanitize SMTP errors to prevent credential exposure (Requirements 10.3)
            log.error("SMTP connection failed while sending email to: {} - {}", to, LogSanitizer.sanitizeException(e));
            return false;
        } catch (Exception e) {
            // Sanitize unexpected errors (Requirements 10.3)
            log.error("Unexpected error while sending email to: {} - {}", to, LogSanitizer.sanitizeException(e));
            return false;
        }
    }


    @Override
    @Async("emailExecutor")
    public void sendPaymentSuccessEmail(Payment payment, Order order, User user) {
        try {
            String htmlContent = templateUtil.buildPaymentSuccessEmail(payment, order, user);
            String subject = "Payment Successful - Order #" + order.getId();
            
            if (sendHtmlEmail(user.getEmail(), subject, htmlContent)) {
                log.info("Payment success email sent to customer: {}", user.getEmail());
            }
        } catch (Exception e) {
            // Sanitize exception to prevent sensitive data exposure (Requirements 10.3)
            log.error("Failed to send payment success email to customer: {} - {}", 
                     user.getEmail(), LogSanitizer.sanitizeException(e));
            // Do not rethrow - email failure should not affect business operations
        }
    }

    @Override
    @Async("emailExecutor")
    public void sendOrderConfirmationToCustomer(Order order, User user) {
        try {
            String htmlContent = templateUtil.buildOrderConfirmationEmail(order, user);
            String subject = "Order Confirmation - Order #" + order.getId();
            
            if (sendHtmlEmail(user.getEmail(), subject, htmlContent)) {
                log.info("Order confirmation email sent to customer: {}", user.getEmail());
            }
        } catch (Exception e) {
            // Sanitize exception to prevent sensitive data exposure (Requirements 10.3)
            log.error("Failed to send order confirmation email to customer: {} - {}", 
                     user.getEmail(), LogSanitizer.sanitizeException(e));
        }
    }

    @Override
    @Async("emailExecutor")
    public void sendOrderNotificationToAdmin(Order order, User user) {
        try {
            String htmlContent = templateUtil.buildAdminOrderNotificationEmail(order, user);
            String subject = "New Order Received - Order #" + order.getId();
            
            if (sendHtmlEmail(adminEmail, subject, htmlContent)) {
                log.info("Order notification email sent to admin: {}", adminEmail);
            }
        } catch (Exception e) {
            // Sanitize exception to prevent sensitive data exposure (Requirements 10.3)
            log.error("Failed to send order notification email to admin - {}", LogSanitizer.sanitizeException(e));
        }
    }

    @Override
    @Async("emailExecutor")
    public void sendOrderCancellationToCustomer(Order order, User user) {
        try {
            String htmlContent = templateUtil.buildOrderCancellationEmail(order, user);
            String subject = "Order Cancelled - Order #" + order.getId();
            
            if (sendHtmlEmail(user.getEmail(), subject, htmlContent)) {
                log.info("Order cancellation email sent to customer: {}", user.getEmail());
            }
        } catch (Exception e) {
            // Sanitize exception to prevent sensitive data exposure (Requirements 10.3)
            log.error("Failed to send order cancellation email to customer: {} - {}", 
                     user.getEmail(), LogSanitizer.sanitizeException(e));
        }
    }

    @Override
    @Async("emailExecutor")
    public void sendOrderCancellationToAdmin(Order order, User user) {
        try {
            String htmlContent = templateUtil.buildAdminCancellationEmail(order, user);
            String subject = "Order Cancelled - Order #" + order.getId();
            
            if (sendHtmlEmail(adminEmail, subject, htmlContent)) {
                log.info("Order cancellation notification sent to admin: {}", adminEmail);
            }
        } catch (Exception e) {
            // Sanitize exception to prevent sensitive data exposure (Requirements 10.3)
            log.error("Failed to send order cancellation email to admin - {}", LogSanitizer.sanitizeException(e));
        }
    }


    @Override
    @Async("emailExecutor")
    public void sendCartReminderEmail(User user, Product product) {
        try {
            String htmlContent = templateUtil.buildCartReminderEmail(user, product);
            String subject = "Don't Forget Your Cart! Complete Your Purchase";
            
            if (sendHtmlEmail(user.getEmail(), subject, htmlContent)) {
                log.info("Cart reminder email sent to: {}", user.getEmail());
            }
        } catch (Exception e) {
            // Sanitize exception to prevent sensitive data exposure (Requirements 10.3)
            log.error("Failed to send cart reminder email to: {} - {}", 
                     user.getEmail(), LogSanitizer.sanitizeException(e));
        }
    }

    @Override
    @Async("emailExecutor")
    public void sendWishlistReminderEmail(User user, Product product) {
        try {
            String htmlContent = templateUtil.buildWishlistReminderEmail(user, product);
            String subject = "Your Wishlist Item is Waiting! Buy Now";
            
            if (sendHtmlEmail(user.getEmail(), subject, htmlContent)) {
                log.info("Wishlist reminder email sent to: {}", user.getEmail());
            }
        } catch (Exception e) {
            // Sanitize exception to prevent sensitive data exposure (Requirements 10.3)
            log.error("Failed to send wishlist reminder email to: {} - {}", 
                     user.getEmail(), LogSanitizer.sanitizeException(e));
        }
    }

    @Override
    @Async("emailExecutor")
    public void sendInvoiceToCustomer(Invoice invoice, User user, byte[] pdfData) {
        try {
            String htmlContent = templateUtil.buildInvoiceEmail(invoice, user);
            String subject = "Invoice for Order #" + invoice.getOrderId();
            String attachmentName = invoice.getInvoiceNumber() + ".pdf";
            
            if (sendHtmlEmailWithAttachment(user.getEmail(), subject, htmlContent, attachmentName, pdfData)) {
                log.info("Invoice email sent to customer: {}", user.getEmail());
            }
        } catch (Exception e) {
            // Sanitize exception to prevent sensitive data exposure (Requirements 10.3)
            log.error("Failed to send invoice email to customer: {} - {}", 
                     user.getEmail(), LogSanitizer.sanitizeException(e));
        }
    }

    @Override
    @Async("emailExecutor")
    public void sendInvoiceToAdmin(Invoice invoice, User user, byte[] pdfData) {
        try {
            String htmlContent = templateUtil.buildAdminInvoiceEmail(invoice, user);
            String subject = "Invoice Generated - Order #" + invoice.getOrderId();
            String attachmentName = invoice.getInvoiceNumber() + ".pdf";
            
            if (sendHtmlEmailWithAttachment(adminEmail, subject, htmlContent, attachmentName, pdfData)) {
                log.info("Invoice email sent to admin: {}", adminEmail);
            }
        } catch (Exception e) {
            // Sanitize exception to prevent sensitive data exposure (Requirements 10.3)
            log.error("Failed to send invoice email to admin - {}", LogSanitizer.sanitizeException(e));
        }
    }
}
