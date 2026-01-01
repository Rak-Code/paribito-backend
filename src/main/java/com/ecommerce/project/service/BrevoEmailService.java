package com.ecommerce.project.service;

import com.ecommerce.project.entity.Invoice;
import com.ecommerce.project.entity.Order;
import com.ecommerce.project.entity.Payment;
import com.ecommerce.project.entity.Product;
import com.ecommerce.project.entity.User;
import com.ecommerce.project.util.EmailTemplateUtil;
import com.ecommerce.project.util.LogSanitizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * Brevo (Sendinblue) email service implementation using HTTPS API.
 * This bypasses SMTP port restrictions on cloud platforms like Render.
 * 
 * Free tier: 300 emails/day
 * Uses port 443 (HTTPS) - never blocked by cloud providers.
 */
@Service
@ConditionalOnProperty(name = "email.provider", havingValue = "brevo", matchIfMissing = false)
@RequiredArgsConstructor
@Slf4j
public class BrevoEmailService implements EmailService {

    private final EmailTemplateUtil templateUtil;

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${email.from}")
    private String fromEmail;

    @Value("${email.from.name:Paribito}")
    private String fromName;

    @Value("${email.admin}")
    private String adminEmail;

    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    private RestClient getRestClient() {
        return RestClient.builder()
                .baseUrl(BREVO_API_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("api-key", apiKey)
                .build();
    }


    /**
     * Validates an email address using a basic regex pattern.
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    /**
     * Sends an HTML email via Brevo API.
     */
    private boolean sendHtmlEmail(String to, String subject, String htmlContent) {
        log.info("=== BREVO EMAIL: Attempting to send email to: {}, subject: {}", to, subject);

        if (!isValidEmail(to)) {
            log.warn("=== BREVO VALIDATION: Invalid email address, skipping: {}", to);
            return false;
        }

        try {
            Map<String, Object> emailRequest = Map.of(
                "sender", Map.of("name", fromName, "email", fromEmail),
                "to", List.of(Map.of("email", to)),
                "subject", subject,
                "htmlContent", htmlContent
            );

            String response = getRestClient()
                .post()
                .body(emailRequest)
                .retrieve()
                .body(String.class);

            log.info("=== BREVO SUCCESS: Email sent to: {}, response: {}", to, response);
            return true;

        } catch (Exception e) {
            log.error("=== BREVO ERROR: Failed to send email to: {} - {}", to, LogSanitizer.sanitizeException(e));
            return false;
        }
    }

    /**
     * Sends an HTML email with PDF attachment via Brevo API.
     */
    private boolean sendHtmlEmailWithAttachment(String to, String subject, String htmlContent,
                                                 String attachmentName, byte[] attachmentData) {
        log.info("=== BREVO EMAIL: Sending email with attachment to: {}", to);

        if (!isValidEmail(to)) {
            log.warn("=== BREVO VALIDATION: Invalid email address, skipping: {}", to);
            return false;
        }

        try {
            Map<String, Object> emailRequest;
            
            if (attachmentData != null && attachmentData.length > 0) {
                String base64Content = Base64.getEncoder().encodeToString(attachmentData);
                emailRequest = Map.of(
                    "sender", Map.of("name", fromName, "email", fromEmail),
                    "to", List.of(Map.of("email", to)),
                    "subject", subject,
                    "htmlContent", htmlContent,
                    "attachment", List.of(Map.of(
                        "name", attachmentName,
                        "content", base64Content
                    ))
                );
            } else {
                emailRequest = Map.of(
                    "sender", Map.of("name", fromName, "email", fromEmail),
                    "to", List.of(Map.of("email", to)),
                    "subject", subject,
                    "htmlContent", htmlContent
                );
            }

            String response = getRestClient()
                .post()
                .body(emailRequest)
                .retrieve()
                .body(String.class);

            log.info("=== BREVO SUCCESS: Email with attachment sent to: {}", to);
            return true;

        } catch (Exception e) {
            log.error("=== BREVO ERROR: Failed to send email with attachment to: {} - {}", 
                     to, LogSanitizer.sanitizeException(e));
            return false;
        }
    }


    // ==================== EmailService Interface Implementation ====================

    @Override
    @Async("emailExecutor")
    public void sendPaymentSuccessEmail(Payment payment, Order order, User user) {
        log.info("=== BREVO: Starting sendPaymentSuccessEmail for user: {}, order: {}", 
                user.getEmail(), order.getId());
        try {
            String htmlContent = templateUtil.buildPaymentSuccessEmail(payment, order, user);
            String subject = "Payment Successful - Order #" + order.getId();
            
            if (sendHtmlEmail(user.getEmail(), subject, htmlContent)) {
                log.info("=== BREVO SUCCESS: Payment success email sent to: {}", user.getEmail());
            }
        } catch (Exception e) {
            log.error("=== BREVO ERROR: Failed to send payment success email: {} - {}", 
                     user.getEmail(), LogSanitizer.sanitizeException(e));
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
            log.error("Failed to send order confirmation email: {} - {}", 
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
            log.error("Failed to send order cancellation email: {} - {}", 
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
            log.error("Failed to send cart reminder email: {} - {}", 
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
            log.error("Failed to send wishlist reminder email: {} - {}", 
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
            log.error("Failed to send invoice email: {} - {}", 
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
            log.error("Failed to send invoice email to admin - {}", LogSanitizer.sanitizeException(e));
        }
    }
}
