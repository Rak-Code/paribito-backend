package com.ecommerce.project.repository;

import com.ecommerce.project.entity.PasswordResetOtp;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetOtpRepository extends MongoRepository<PasswordResetOtp, String> {
    
    Optional<PasswordResetOtp> findByEmailAndIsUsedFalse(String email);
    
    Optional<PasswordResetOtp> findByEmailAndOtpCodeAndIsUsedFalse(String email, String otpCode);
    
    @Query("{ 'email': ?0, 'isUsed': false, 'expiresAt': { $gt: ?1 } }")
    Optional<PasswordResetOtp> findValidOtpByEmail(String email, java.time.LocalDateTime now);
    
    @Query("{ 'email': ?0, 'otpCode': ?1, 'isUsed': false, 'expiresAt': { $gt: ?2 } }")
    Optional<PasswordResetOtp> findValidOtpByEmailAndCode(String email, String otpCode, java.time.LocalDateTime now);
    
    void deleteByEmail(String email);
}
