package com.ecommerce.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserRegisterDTO(
        @Schema(description = "Full name of the user", example = "John Doe")
        String fullName,
        @Schema(description = "Email address of the user", example = "john.doe@example.com")
        String email,
        @Schema(description = "Password for the user account", example = "password123")
        String password,
        @Schema(description = "Phone number of the user", example = "+1234567890")
        String phone,
        @Schema(description = "Role of the user", example = "USER")
        String role
) {}
