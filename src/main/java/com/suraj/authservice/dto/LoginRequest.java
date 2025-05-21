
package com.suraj.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

/**
 * Data Transfer Object for user login requests.
 * Contains username and password for authentication.
 */
public record LoginRequest(
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        @Schema(description = "Username for authentication", example = "johnsmith")
        String username,
        
        @NotBlank(message = "Password is required")
        @Schema(description = "Password for authentication", example = "Password@123")
        String password
) implements Serializable {
}