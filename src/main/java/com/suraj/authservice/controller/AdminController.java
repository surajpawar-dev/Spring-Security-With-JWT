package com.suraj.authservice.controller;

import com.suraj.authservice.dto.ApiResponse;
import com.suraj.authservice.dto.RoleChangeRequest;
import com.suraj.authservice.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for administrative operations.
 * All endpoints in this controller require ADMIN role.
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Administration", description = "Administrative endpoints for user management (ADMIN only)")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final AdminService adminService;

    /**
     * Endpoint to change a user's role.
     * Only accessible to administrators.
     *
     * @param request The role change request containing username and new role
     * @return ResponseEntity with the result of the operation
     */
    @PutMapping("/users/role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(
            summary = "Change user role",
            description = "Allows administrators to upgrade or downgrade a user's role. " +
                    "This operation is audited and can only be performed by users with ADMIN role."
    )
    public ResponseEntity<ApiResponse<String>> changeUserRole(
            @Parameter(description = "Role change details", required = true)
            @Valid @RequestBody RoleChangeRequest request) {
        
        log.debug("Role change request received for user: {}", request.getUsername());
        
        String result = adminService.changeUserRole(
                request.getUsername(),
                request.getNewRole(),
                request.getReason()
        );
        
        ApiResponse<String> response = new ApiResponse<>(
                200,
                true,
                "Role updated successfully",
                result
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get information about the role change system.
     * This endpoint is for informational purposes.
     *
     * @return ResponseEntity with information about available roles
     */
    @GetMapping("/roles/info")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(
            summary = "Get role system information",
            description = "Provides information about the available roles and role management capabilities"
    )
    public ResponseEntity<ApiResponse<String>> getRoleSystemInfo() {
        ApiResponse<String> response = new ApiResponse<>(
                200,
                true,
                "Role System Information",
                "The system supports the following roles: ROLE_USER (basic access), ROLE_ADMIN (full access). " +
                        "Role changes are audited and can only be performed by administrators."
        );
        
        return ResponseEntity.ok(response);
    }
}
