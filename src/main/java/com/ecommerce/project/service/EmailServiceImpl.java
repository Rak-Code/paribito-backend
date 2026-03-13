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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of EmailService that sends HTML-formatted emails
 * using templates from EmailTemplateUtil.
 * 
 * All email operations are asynchronous and include comprehensive error handling
 * to ensure email failures do not affect core business operations.
 */
@Service
@ConditionalOnProperty(name = "email.provider", havingValue = "smtp-disabled", matchIfMissing = false)
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final EmailTemplateUtil templateUtil;

    @Value("${email.from}")
    private String fromEmail;

    @Value("${email.admin}")
    private String adminEmail;

    private List<String> getAdminRecipients() {
        if (adminEmail == null || adminEmail.isBlank()) {
            return List.of();
        }

        String[] raw = adminEmail.split("[;,]");
        List<String> recipients = new ArrayList<>();
        for (String r : raw) {
            String candidate = r == null ? null : r.trim();
            if (isValidEmail(candidate)) {
                recipients.add(candidate);
            }
        }
        return recipients;
    }

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
        log.info("=== EMAIL DEBUG: Attempting to send email to: {}, subject: {}", to, subject);
        
        if (!isValidEmail(to)) {
            log.warn("=== EMAIL VALIDATION: Invalid email address, skipping email send: {}", to);
            return false;
        }

        try {
            log.info("=== EMAIL DEBUG: Creating MimeMessage for: {}", to);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true indicates HTML content
            
            log.info("=== EMAIL DEBUG: Sending email via JavaMailSender to: {}", to);
            mailSender.send(message);
            log.info("=== EMAIL SUCCESS: Email sent successfully to: {}", to);
            return true;
        } catch (MessagingException e) {
            // Sanitize exception to prevent sensitive data exposure (Requirements 10.3)
            log.error("=== EMAIL ERROR: Failed to create email message for: {} - {}", to, LogSanitizer.sanitizeException(e));
            return false;
        } catch (MailException e) {
            // Sanitize SMTP errors to prevent credential exposure (Requirements 10.3)
            log.error("=== EMAIL ERROR: SMTP connection failed while sending email to: {} - {}", to, LogSanitizer.sanitizeException(e));
            return false;
        } catch (Exception e) {
            // Sanitize unexpected errors (Requirements 10.3)
            log.error("=== EMAIL ERROR: Unexpected error while sending email to: {} - {}", to, LogSanitizer.sanitizeException(e));
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
        log.info("=== EMAIL DEBUG: Starting sendPaymentSuccessEmail for user: {}, order: {}", 
                user.getEmail(), order.getId());
        try {
            String htmlContent = templateUtil.buildPaymentSuccessEmail(payment, order, user);
            String subject = "Payment Successful - Order #" + order.getId();
            
            log.info("=== EMAIL DEBUG: Generated email content, attempting to send to: {}", user.getEmail());
            
            if (sendHtmlEmail(user.getEmail(), subject, htmlContent)) {
                log.info("=== EMAIL SUCCESS: Payment success email sent to customer: {}", user.getEmail());
            } else {
                log.error("=== EMAIL FAILED: Payment success email failed to send to customer: {}", user.getEmail());
            }
        } catch (Exception e) {
            // Sanitize exception to prevent sensitive data exposure (Requirements 10.3)
            log.error("=== EMAIL ERROR: Failed to send payment success email to customer: {} - {}", 
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

            List<String> recipients = getAdminRecipients();
            if (recipients.isEmpty()) {
                log.warn("No valid admin recipients configured, skipping admin order notification for order: {}", order.getId());
                return;
            }

            for (String recipient : recipients) {
                if (sendHtmlEmail(recipient, subject, htmlContent)) {
                    log.info("Order notification email sent to admin: {}", recipient);
                } else {
                    log.warn("Failed to send order notification email to admin: {}", recipient);
                }
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

            List<String> recipients = getAdminRecipients();
            if (recipients.isEmpty()) {
                log.warn("No valid admin recipients configured, skipping admin cancellation notification for order: {}", order.getId());
                return;
            }

            for (String recipient : recipients) {
                if (sendHtmlEmail(recipient, subject, htmlContent)) {
                    log.info("Order cancellation notification sent to admin: {}", recipient);
                } else {
                    log.warn("Failed to send order cancellation notification to admin: {}", recipient);
                }
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

            List<String> recipients = getAdminRecipients();
            if (recipients.isEmpty()) {
                log.warn("No valid admin recipients configured, skipping invoice email for invoice: {}", invoice.getInvoiceNumber());
                return;
            }

            for (String recipient : recipients) {
                if (sendHtmlEmailWithAttachment(recipient, subject, htmlContent, attachmentName, pdfData)) {
                    log.info("Invoice email sent to admin: {}", recipient);
                } else {
                    log.warn("Failed to send invoice email to admin: {}", recipient);
                }
            }
        } catch (Exception e) {
            // Sanitize exception to prevent sensitive data exposure (Requirements 10.3)
            log.error("Failed to send invoice email to admin - {}", LogSanitizer.sanitizeException(e));
        }
    }

    @Override
    @Async("emailExecutor")
    public void sendOtpEmail(String toEmail, String otpCode) {
        try {
            String htmlContent = buildOtpEmailHtml(otpCode);
            String subject = "Password Reset OTP - Paribito";
            
            if (sendHtmlEmail(toEmail, subject, htmlContent)) {
                log.info("OTP email sent successfully to: {}", toEmail);
            } else {
                log.warn("Failed to send OTP email to: {}", toEmail);
            }
        } catch (Exception e) {
            // Sanitize exception to prevent sensitive data exposure (Requirements 10.3)
            log.error("Failed to send OTP email to: {} - {}", toEmail, LogSanitizer.sanitizeException(e));
        }
    }

    private String buildOtpEmailHtml(String otpCode) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Password Reset OTP</title>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { text-align: center; margin-bottom: 30px; }
                        .otp-box { background-color: #f8f9fa; border: 2px dashed #007bff; padding: 20px; text-align: center; margin: 20px 0; }
                        .otp-code { font-size: 32px; font-weight: bold; color: #007bff; letter-spacing: 5px; }
                        .footer { margin-top: 30px; padding-top: 20px; border-top: 1px solid #eee; font-size: 12px; color: #666; }
                        .warning { background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 10px; margin: 20px 0; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Paribito - Password Reset</h1>
                        </div>
                        
                        <p>Hello,</p>
                        
                        <p>You have requested to reset your password for your Paribito account. Use the OTP code below to proceed:</p>
                        
                        <div class="otp-box">
                            <div class="otp-code">%s</div>
                        </div>
                        
                        <div class="warning">
                            <strong>Important:</strong> This OTP will expire in 10 minutes for security reasons. Please use it immediately.
                        </div>
                        
                        <p>If you didn't request this password reset, please ignore this email or contact our support team.</p>
                        
                        <div class="footer">
                            <p>This is an automated message from Paribito. Please do not reply to this email.</p>
                            <p>&copy; 2024 Paribito. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(otpCode);
    }
}
