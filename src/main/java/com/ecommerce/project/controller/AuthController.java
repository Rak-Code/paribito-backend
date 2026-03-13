package com.ecommerce.project.controller;

import com.ecommerce.project.dto.AuthResponseDTO;
import com.ecommerce.project.dto.UserLoginDTO;
import com.ecommerce.project.dto.UserRegisterDTO;
import com.ecommerce.project.dto.UserResponseDTO;
import com.ecommerce.project.dto.ForgotPasswordRequestDTO;
import com.ecommerce.project.dto.VerifyOtpRequestDTO;
import com.ecommerce.project.dto.ResetPasswordRequestDTO;
import com.ecommerce.project.entity.User;
import com.ecommerce.project.security.JwtUtil;
import com.ecommerce.project.service.UserService;
import com.ecommerce.project.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API for user authentication and registration")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordResetService passwordResetService;

    @PostMapping("/register")
    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account with the provided registration details"
    )
    @ApiResponse(responseCode = "201", description = "User registered successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input data",
        content = @Content)
    @ApiResponse(responseCode = "409", description = "User already exists",
        content = @Content)
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody @Parameter(schema = @Schema(implementation = UserRegisterDTO.class)) UserRegisterDTO dto) {
        User saved = userService.register(dto);
        UserResponseDTO response = new UserResponseDTO(
            saved.getId(),
            saved.getEmail(),
            saved.getFullName(),
            saved.getPhone(),
            saved.getRole(),
            saved.getCreatedAt(),
            saved.getAddresses()
        );
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/login")
    @Operation(
        summary = "Authenticate user",
        description = "Authenticates user credentials and returns JWT token"
    )
    @ApiResponse(responseCode = "200", description = "Login successful",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponseDTO.class)))
    @ApiResponse(responseCode = "401", description = "Invalid credentials",
        content = @Content)
    @ApiResponse(responseCode = "400", description = "Invalid input data",
        content = @Content)
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody @Parameter(schema = @Schema(implementation = UserLoginDTO.class)) UserLoginDTO dto) {
        User user = userService.login(dto);
        // Generate token
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
        String token = jwtUtil.generateToken(userDetails);
        
        UserResponseDTO userResponse = new UserResponseDTO(
            user.getId(),
            user.getEmail(),
            user.getFullName(),
            user.getPhone(),
            user.getRole(),
            user.getCreatedAt(),
            user.getAddresses()
        );
        
        return ResponseEntity.ok(new AuthResponseDTO(token, userResponse));
    }

    @PostMapping("/forgot-password")
    @Operation(
        summary = "Initiate password reset",
        description = "Sends an OTP to the user's email for password reset"
    )
    @ApiResponse(responseCode = "200", description = "OTP sent successfully",
        content = @Content)
    @ApiResponse(responseCode = "400", description = "Invalid email format",
        content = @Content)
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        boolean success = passwordResetService.initiatePasswordReset(request.getEmail());
        if (success) {
            return ResponseEntity.ok(Map.of("message", "OTP sent to your email if account exists"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to send OTP"));
        }
    }

    @PostMapping("/verify-otp")
    @Operation(
        summary = "Verify OTP code",
        description = "Verifies the OTP code for password reset"
    )
    @ApiResponse(responseCode = "200", description = "OTP verified successfully",
        content = @Content)
    @ApiResponse(responseCode = "400", description = "Invalid or expired OTP",
        content = @Content)
    public ResponseEntity<Map<String, String>> verifyOtp(@Valid @RequestBody VerifyOtpRequestDTO request) {
        boolean isValid = passwordResetService.verifyOtp(request.getEmail(), request.getOtpCode());
        if (isValid) {
            return ResponseEntity.ok(Map.of("message", "OTP verified successfully"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid or expired OTP"));
        }
    }

    @PostMapping("/reset-password")
    @Operation(
        summary = "Reset password",
        description = "Resets the user's password after OTP verification"
    )
    @ApiResponse(responseCode = "200", description = "Password reset successfully",
        content = @Content)
    @ApiResponse(responseCode = "400", description = "Invalid OTP or password mismatch",
        content = @Content)
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
        // Validate password match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Passwords do not match"));
        }

        boolean success = passwordResetService.resetPassword(
            request.getEmail(), 
            request.getOtpCode(), 
            request.getNewPassword()
        );
        
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid OTP or password reset failed"));
        }
    }
}
