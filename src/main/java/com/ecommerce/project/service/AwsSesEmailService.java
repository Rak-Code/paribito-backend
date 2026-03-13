package com.ecommerce.project.service;

import com.ecommerce.project.entity.Invoice;
import com.ecommerce.project.entity.Order;
import com.ecommerce.project.entity.Payment;
import com.ecommerce.project.entity.Product;
import com.ecommerce.project.entity.User;
import com.ecommerce.project.util.EmailTemplateUtil;
import com.ecommerce.project.util.LogSanitizer;
import com.ecommerce.project.util.MimeMessageBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.RawMessage;
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest;

import java.util.Base64;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * AWS SES email service implementation using RestClient.
 * Sends emails via AWS SES SMTP API v2.
 * Only loaded when email.provider=aws-ses.
 */
@Service
@ConditionalOnProperty(name = "email.provider", havingValue = "aws-ses", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class AwsSesEmailService implements EmailService {

    private final SesClient sesClient;
    private final EmailTemplateUtil templateUtil;

    @Value("${email.from}")
    private String fromEmail;

    @Value("${email.from.name:Paribito}")
    private String fromName;

    @Value("${email.admin}")
    private String adminEmail;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    @Override
    @Async("emailExecutor")
    public void sendPaymentSuccessEmail(Payment payment, Order order, User user) {
        try {
            log.info("Sending payment success email to: {}", user.getEmail());
            String htmlContent = templateUtil.buildPaymentSuccessEmail(payment, order, user);
            String subject = "Payment Successful - Order " + order.getId();
            
            boolean success = sendHtmlEmail(user.getEmail(), subject, htmlContent);
            
            if (success) {
                log.info("Payment success email sent successfully to: {}", user.getEmail());
            } else {
                log.warn("Failed to send payment success email to: {}", user.getEmail());
            }
        } catch (Exception e) {
            log.error("Error sending payment success email to {}: {}", 
                user.getEmail(), LogSanitizer.sanitizeException(e));
        }
    }

    @Override
    @Async("emailExecutor")
    public void sendOrderConfirmationToCustomer(Order order, User user) {
        try {
            log.info("Sending order confirmation email to: {}", user.getEmail());
            String htmlContent = templateUtil.buildOrderConfirmationEmail(order, user);
            String subject = "Order Confirmation - Order " + order.getId();
            
            boolean success = sendHtmlEmail(user.getEmail(), subject, htmlContent);
            
            if (success) {
                log.info("Order confirmation email sent successfully to: {}", user.getEmail());
            } else {
                log.warn("Failed to send order confirmation email to: {}", user.getEmail());
            }
        } catch (Exception e) {
            log.error("Error sending order confirmation email to {}: {}", 
                user.getEmail(), LogSanitizer.sanitizeException(e));
        }
    }

    @Override
    @Async("emailExecutor")
    public void sendOrderNotificationToAdmin(Order order, User user) {
        try {
            List<String> recipients = getAdminRecipients();
            if (recipients.isEmpty()) {
                log.warn("No valid admin recipients configured, skipping admin order notification for order: {}", order.getId());
                return;
            }

            log.info("Sending order notification email to admin recipients: {}", recipients);
            String htmlContent = templateUtil.buildAdminOrderNotificationEmail(order, user);
            String subject = "New Order Received - Order " + order.getId();

            for (String recipient : recipients) {
                boolean success = sendHtmlEmail(recipient, subject, htmlContent);
                if (success) {
                    log.info("Order notification email sent successfully to admin: {}", recipient);
                } else {
                    log.warn("Failed to send order notification email to admin: {}", recipient);
                }
            }
        } catch (Exception e) {
            log.error("Error sending order notification email to admin recipients {}: {}",
                LogSanitizer.sanitize(adminEmail), LogSanitizer.sanitizeException(e));
        }
    }

    @Override
    @Async("emailExecutor")
    public void sendOrderCancellationToCustomer(Order order, User user) {
        try {
            log.info("Sending order cancellation email to: {}", user.getEmail());
            String htmlContent = templateUtil.buildOrderCancellationEmail(order, user);
            String subject = "Order Cancelled - Order " + order.getId();
            
            boolean success = sendHtmlEmail(user.getEmail(), subject, htmlContent);
            
            if (success) {
                log.info("Order cancellation email sent successfully to: {}", user.getEmail());
            } else {
                log.warn("Failed to send order cancellation email to: {}", user.getEmail());
            }
        } catch (Exception e) {
            log.error("Error sending order cancellation email to {}: {}", 
                user.getEmail(), LogSanitizer.sanitizeException(e));
        }
    }

    @Override
    @Async("emailExecutor")
    public void sendOrderCancellationToAdmin(Order order, User user) {
        try {
            List<String> recipients = getAdminRecipients();
            if (recipients.isEmpty()) {
                log.warn("No valid admin recipients configured, skipping admin cancellation notification for order: {}", order.getId());
                return;
            }

            log.info("Sending order cancellation notification to admin recipients: {}", recipients);
            String htmlContent = templateUtil.buildAdminCancellationEmail(order, user);
            String subject = "Order Cancelled - Order " + order.getId();

            for (String recipient : recipients) {
                boolean success = sendHtmlEmail(recipient, subject, htmlContent);
                if (success) {
                    log.info("Order cancellation notification sent successfully to admin: {}", recipient);
                } else {
                    log.warn("Failed to send order cancellation notification to admin: {}", recipient);
                }
            }
        } catch (Exception e) {
            log.error("Error sending order cancellation notification to admin recipients {}: {}",
                LogSanitizer.sanitize(adminEmail), LogSanitizer.sanitizeException(e));
        }
    }

    @Override
    @Async("emailExecutor")
    public void sendCartReminderEmail(User user, Product product) {
        try {
            log.info("Sending cart reminder email to: {}", user.getEmail());
            String htmlContent = templateUtil.buildCartReminderEmail(user, product);
            String subject = "Don't Forget Your Cart - " + product.getName();
            
            boolean success = sendHtmlEmail(user.getEmail(), subject, htmlContent);
            
            if (success) {
                log.info("Cart reminder email sent successfully to: {}", user.getEmail());
            } else {
                log.warn("Failed to send cart reminder email to: {}", user.getEmail());
            }
        } catch (Exception e) {
            log.error("Error sending cart reminder email to {}: {}", 
                user.getEmail(), LogSanitizer.sanitizeException(e));
        }
    }

    @Override
    @Async("emailExecutor")
    public void sendWishlistReminderEmail(User user, Product product) {
        try {
            log.info("Sending wishlist reminder email to: {}", user.getEmail());
            String htmlContent = templateUtil.buildWishlistReminderEmail(user, product);
            String subject = "Your Wishlist Item Awaits - " + product.getName();
            
            boolean success = sendHtmlEmail(user.getEmail(), subject, htmlContent);
            
            if (success) {
                log.info("Wishlist reminder email sent successfully to: {}", user.getEmail());
            } else {
                log.warn("Failed to send wishlist reminder email to: {}", user.getEmail());
            }
        } catch (Exception e) {
            log.error("Error sending wishlist reminder email to {}: {}", 
                user.getEmail(), LogSanitizer.sanitizeException(e));
        }
    }

    @Override
    @Async("emailExecutor")
    public void sendInvoiceToCustomer(Invoice invoice, User user, byte[] pdfData) {
        try {
            log.info("Sending invoice email to customer: {}", user.getEmail());
            String htmlContent = templateUtil.buildInvoiceEmail(invoice, user);
            String subject = "Your Invoice - " + invoice.getInvoiceNumber();
            String filename = invoice.getInvoiceNumber() + ".pdf";
            
            boolean success = sendHtmlEmailWithAttachment(
                user.getEmail(), subject, htmlContent, pdfData, filename);
            
            if (success) {
                log.info("Invoice email sent successfully to customer: {}", user.getEmail());
            } else {
                log.warn("Failed to send invoice email to customer: {}", user.getEmail());
            }
        } catch (Exception e) {
            log.error("Error sending invoice email to customer {}: {}", 
                user.getEmail(), LogSanitizer.sanitizeException(e));
        }
    }

    @Override
    @Async("emailExecutor")
    public void sendInvoiceToAdmin(Invoice invoice, User user, byte[] pdfData) {
        try {
            List<String> recipients = getAdminRecipients();
            if (recipients.isEmpty()) {
                log.warn("No valid admin recipients configured, skipping invoice email for invoice: {}", invoice.getInvoiceNumber());
                return;
            }

            log.info("Sending invoice email to admin recipients: {}", recipients);
            String htmlContent = templateUtil.buildAdminInvoiceEmail(invoice, user);
            String subject = "Invoice Generated - " + invoice.getInvoiceNumber();
            String filename = invoice.getInvoiceNumber() + ".pdf";

            for (String recipient : recipients) {
                boolean success = sendHtmlEmailWithAttachment(
                    recipient, subject, htmlContent, pdfData, filename);
                if (success) {
                    log.info("Invoice email sent successfully to admin: {}", recipient);
                } else {
                    log.warn("Failed to send invoice email to admin: {}", recipient);
                }
            }
        } catch (Exception e) {
            log.error("Error sending invoice email to admin recipients {}: {}",
                LogSanitizer.sanitize(adminEmail), LogSanitizer.sanitizeException(e));
        }
    }

    @Override
    @Async("emailExecutor")
    public void sendOtpEmail(String toEmail, String otpCode) {
        try {
            log.info("Sending OTP email to: {}", toEmail);
            String htmlContent = buildOtpEmailHtml(otpCode);
            String subject = "Password Reset OTP - Paribito";
            
            boolean success = sendHtmlEmail(toEmail, subject, htmlContent);
            
            if (success) {
                log.info("OTP email sent successfully to: {}", toEmail);
            } else {
                log.warn("Failed to send OTP email to: {}", toEmail);
            }
        } catch (Exception e) {
            log.error("Error sending OTP email to {}: {}", 
                toEmail, LogSanitizer.sanitizeException(e));
        }
    }

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
     * Validates an email address using regex pattern.
     * Returns false for null, empty, or invalid email formats.
     *
     * @param email the email address to validate
     * @return true if email is valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Sends an HTML email via AWS SES SMTP API.
     * Validates recipient email, builds MIME message, and handles HTTP responses.
     *
     * @param to recipient email address
     * @param subject email subject
     * @param htmlContent HTML content of the email
     * @return true if email sent successfully, false otherwise
     */
    private boolean sendHtmlEmail(String to, String subject, String htmlContent) {
        // Validate recipient email
        if (!isValidEmail(to)) {
            log.warn("Invalid recipient email address: {}", LogSanitizer.sanitize(to));
            return false;
        }

        try {
            // Build MIME message
            String base64MimeMessage = MimeMessageBuilder.buildSimpleMimeMessage(
                    fromEmail,
                    fromName,
                    to,
                    subject,
                    htmlContent
            );

            byte[] rawBytes = Base64.getDecoder().decode(base64MimeMessage);
            SendRawEmailRequest request = SendRawEmailRequest.builder()
                    .rawMessage(RawMessage.builder().data(SdkBytes.fromByteArray(rawBytes)).build())
                    .build();

            sesClient.sendRawEmail(request);

            log.info("Email sent successfully to: {}", to);
            return true;

        } catch (Exception e) {
            // Catch any other unexpected errors
            log.error("Unexpected error sending email: {}", LogSanitizer.sanitizeException(e));
            return false;
        }
    }

    /**
     * Sends an HTML email with PDF attachment via AWS SES SMTP API.
     * Validates recipient email, builds MIME message with attachment, and handles errors gracefully.
     *
     * @param to recipient email address
     * @param subject email subject
     * @param htmlContent HTML content of the email
     * @param attachmentData PDF attachment data as byte array
     * @param attachmentName filename for the attachment
     * @return true if email sent successfully, false otherwise
     */
    private boolean sendHtmlEmailWithAttachment(String to, String subject, String htmlContent,
                                                 byte[] attachmentData, String attachmentName) {
        // Validate recipient email
        if (!isValidEmail(to)) {
            log.warn("Invalid recipient email address: {}", LogSanitizer.sanitize(to));
            return false;
        }

        try {
            // Build MIME message with attachment
            String base64MimeMessage = MimeMessageBuilder.buildMimeMessage(
                    fromEmail,
                    fromName,
                    to,
                    subject,
                    htmlContent,
                    attachmentData,
                    attachmentName
            );

            byte[] rawBytes = Base64.getDecoder().decode(base64MimeMessage);
            SendRawEmailRequest request = SendRawEmailRequest.builder()
                    .rawMessage(RawMessage.builder().data(SdkBytes.fromByteArray(rawBytes)).build())
                    .build();

            sesClient.sendRawEmail(request);

            log.info("Email with attachment sent successfully to: {}", to);
            return true;

        } catch (IllegalArgumentException e) {
            // Attachment encoding errors
            log.error("Attachment encoding error: {}", LogSanitizer.sanitizeException(e));
            // Try sending without attachment
            log.warn("Attempting to send email without attachment to: {}", to);
            return sendHtmlEmail(to, subject, htmlContent);

        } catch (Exception e) {
            // Catch any other unexpected errors
            log.error("Unexpected error sending email with attachment: {}", LogSanitizer.sanitizeException(e));
            return false;
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
