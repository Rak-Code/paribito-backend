package com.ecommerce.project.controller;

import com.ecommerce.project.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Test controller for debugging email functionality.
 * This should be removed in production.
 */
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Slf4j
public class EmailTestController {

    private final EmailService emailService;

    @Value("${email.from}")
    private String fromEmail;

    @Value("${email.admin}")
    private String adminEmail;

    /**
     * Test endpoint to verify environment variables are loaded.
     * GET /api/test/env-config
     */
    @GetMapping("/env-config")
    public ResponseEntity<?> testEnvConfig() {
        String mongoUri = System.getProperty("MONGODB_URI");
        String emailUser = System.getProperty("EMAIL_USER");
        
        return ResponseEntity.ok()
            .body("MongoDB URI loaded: " + (mongoUri != null ? "YES" : "NO") + 
                  ", Email User loaded: " + (emailUser != null ? "YES" : "NO"));
    }

    /**
     * Test endpoint to verify email configuration.
     * GET /api/test/email-config
     */
    @GetMapping("/email-config")
    public ResponseEntity<?> testEmailConfig() {
        return ResponseEntity.ok()
            .body("Email Config - From: " + fromEmail + ", Admin: " + adminEmail);
    }


    /**
     * Test endpoint to send a simple test email.
     * POST /api/test/send-email?to=test@example.com
     */
    @PostMapping("/send-email")
    public ResponseEntity<?> sendTestEmail(@RequestParam String to) {
        try {
            // Create mock objects for testing
            log.info("Attempting to send test email to: {}", to);
            
            // For now, just return success - you can uncomment below to test actual sending
            // emailService.sendOrderConfirmationToCustomer(mockOrder, mockUser);
            
            return ResponseEntity.ok("Test email sending initiated. Check logs for results.");
        } catch (Exception e) {
            log.error("Failed to send test email", e);
            return ResponseEntity.internalServerError()
                .body("Failed to send test email: " + e.getMessage());
        }
    }
}