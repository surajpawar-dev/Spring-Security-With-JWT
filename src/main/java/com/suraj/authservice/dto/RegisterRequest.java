package com.suraj.authservice.dto;

import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link com.suraj.authservice.entity.User}
 */
public record RegisterRequest(
        UUID id,
        
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        @Schema(description = "Username for the account", example = "johnsmith")
        String username,
        
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        @Schema(description = "Email address", example = "john.smith@example.com")
        String email,
        
        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number should be valid")
        @Schema(description = "Phone number", example = "+1234567890")
        String phoneNumber,
        
        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", 
                message = "Password must contain at least one digit, one lowercase, one uppercase letter, and one special character")
        @Schema(description = "Password for the account", example = "Password@123")
        String password
       ) implements Serializable {
}