package com.ecommerce.project.service;

import com.ecommerce.project.entity.PasswordResetOtp;
import com.ecommerce.project.entity.User;
import com.ecommerce.project.repository.PasswordResetOtpRepository;
import com.ecommerce.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {

    private final PasswordResetOtpRepository otpRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${password-reset.otp.expiry-minutes:10}")
    private int otpExpiryMinutes;

    /**
     * Initiates password reset process by generating and sending OTP
     * @param email the email address of the user requesting password reset
     * @return true if OTP was sent successfully, false otherwise
     */
    @Transactional
    public boolean initiatePasswordReset(String email) {
        try {
            // Check if user exists
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                log.warn("Password reset requested for non-existent email: {}", email);
                // Don't reveal that email doesn't exist for security
                return true;
            }

            // Delete any existing unused OTPs for this email
            otpRepository.deleteByEmail(email);

            // Generate new OTP
            String otpCode = generateOtpCode();
            LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(otpExpiryMinutes);

            // Save OTP
            PasswordResetOtp otp = new PasswordResetOtp(email, otpCode, expiresAt);
            otpRepository.save(otp);

            // Send OTP email
            emailService.sendOtpEmail(email, otpCode);

            log.info("Password reset OTP sent successfully to: {}", email);
            return true;

        } catch (Exception e) {
            log.error("Error initiating password reset for email {}: {}", email, e.getMessage());
            return false;
        }
    }

    /**
     * Verifies the OTP code for a given email
     * @param email the email address
     * @param otpCode the OTP code to verify
     * @return true if OTP is valid, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean verifyOtp(String email, String otpCode) {
        try {
            Optional<PasswordResetOtp> otpOpt = otpRepository.findValidOtpByEmailAndCode(
                email, otpCode, LocalDateTime.now());

            if (otpOpt.isEmpty()) {
                log.warn("Invalid or expired OTP verification attempt for email: {}", email);
                return false;
            }

            PasswordResetOtp otp = otpOpt.get();
            if (otp.isExpired() || otp.isUsed()) {
                log.warn("OTP verification failed - expired or already used for email: {}", email);
                return false;
            }

            log.info("OTP verified successfully for email: {}", email);
            return true;

        } catch (Exception e) {
            log.error("Error verifying OTP for email {}: {}", email, e.getMessage());
            return false;
        }
    }

    /**
     * Resets the password after OTP verification
     * @param email the email address
     * @param otpCode the OTP code
     * @param newPassword the new password
     * @return true if password was reset successfully, false otherwise
     */
    @Transactional
    public boolean resetPassword(String email, String otpCode, String newPassword) {
        try {
            // Verify OTP first
            Optional<PasswordResetOtp> otpOpt = otpRepository.findValidOtpByEmailAndCode(
                email, otpCode, LocalDateTime.now());

            if (otpOpt.isEmpty()) {
                log.warn("Password reset attempted with invalid OTP for email: {}", email);
                return false;
            }

            PasswordResetOtp otp = otpOpt.get();
            if (otp.isExpired() || otp.isUsed()) {
                log.warn("Password reset failed - OTP expired or already used for email: {}", email);
                return false;
            }

            // Get user and update password
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                log.warn("Password reset attempted for non-existent user: {}", email);
                return false;
            }

            User user = userOpt.get();
            user.setPasswordHash(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            // Mark OTP as used
            otp.markAsUsed();
            otpRepository.save(otp);

            log.info("Password reset successfully for email: {}", email);
            return true;

        } catch (Exception e) {
            log.error("Error resetting password for email {}: {}", email, e.getMessage());
            return false;
        }
    }

    /**
     * Generates a 6-digit OTP code
     * @return the generated OTP code
     */
    private String generateOtpCode() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Generates 6-digit number
        return String.valueOf(otp);
    }
}
