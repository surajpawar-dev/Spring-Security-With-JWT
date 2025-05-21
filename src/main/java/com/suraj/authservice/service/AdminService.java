package com.suraj.authservice.service;

import com.suraj.authservice.entity.Role;

/**
 * Service interface for administration operations.
 * Provides methods for user management operations that require admin privileges.
 */
public interface AdminService {
    
    /**
     * Changes a user's role to the specified new role.
     * This operation should only be performed by administrators.
     *
     * @param username The username of the user whose role needs to be changed
     * @param newRole The new role to assign to the user
     * @param reason Optional reason for the role change (for audit purposes)
     * @return A message indicating the result of the operation
     * @throws com.suraj.authservice.exception.ResourceNotFoundException if the user is not found
     * @throws com.suraj.authservice.exception.InvalidRoleException if the role change is invalid
     */
    String changeUserRole(String username, Role newRole, String reason);
}
