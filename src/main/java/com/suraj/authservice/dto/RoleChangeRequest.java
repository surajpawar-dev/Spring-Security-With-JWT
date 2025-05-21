package com.suraj.authservice.dto;

import com.suraj.authservice.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

/**
 * Data Transfer Object (DTO) for role change requests.
 * Used when an administrator wants to change a user's role.
 */
@Schema(description = "Request object for changing a user's role")
public record RoleChangeRequest(
    @NotBlank(message = "Username is required")
    @Schema(description = "Username of the user whose role will be changed", example = "johndoe")
    String username,
    
    @NotNull(message = "New role is required")
    @Schema(description = "New role to assign to the user", example = "ROLE_ADMIN")
    Role newRole,
    
    @Schema(description = "Optional reason for the role change", example = "Promotion based on performance")
    String reason
) {}