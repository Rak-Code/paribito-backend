package com.ecommerce.project.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "password_reset_otps")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetOtp {

    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String otpCode;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime expiresAt;

    private boolean isUsed = false;

    private LocalDateTime usedAt;

    public PasswordResetOtp(String email, String otpCode, LocalDateTime expiresAt) {
        this.email = email;
        this.otpCode = otpCode;
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public void markAsUsed() {
        this.isUsed = true;
        this.usedAt = LocalDateTime.now();
    }
}
