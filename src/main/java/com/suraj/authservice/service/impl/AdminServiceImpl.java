package com.suraj.authservice.service.impl;

import com.suraj.authservice.entity.Role;
import com.suraj.authservice.entity.User;
import com.suraj.authservice.exception.InvalidRoleException;
import com.suraj.authservice.exception.ResourceNotFoundException;
import com.suraj.authservice.repository.UserRepository;
import com.suraj.authservice.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Implementation of the AdminService interface.
 * Provides functionality for admin-only operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public String changeUserRole(String username, Role newRole, String reason) {
        // Get the current admin username for audit logging
        String adminUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // Find the user by username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        // Get the current role
        Role oldRole = user.getRole();
        
        // Prevent changing your own role (security measure)
        if (username.equals(adminUsername)) {
            throw new InvalidRoleException("Administrators cannot change their own role");
        }
        
        // Only allow changes if the role is actually different
        if (oldRole == newRole) {
            return String.format("User '%s' already has the role %s. No changes made.", username, newRole);
        }
        
        // Update the user's role
        user.setRole(newRole);
        userRepository.save(user);
        
        // Log the role change with detailed information for audit purposes
        String logMessage = String.format(
                "Role change: User '%s' role changed from %s to %s by admin '%s' at %s. Reason: %s",
                username, oldRole, newRole, adminUsername, LocalDateTime.now(),
                reason != null ? reason : "No reason provided"
        );
        
        log.info(logMessage);
        
        return String.format("User '%s' role successfully changed from %s to %s", username, oldRole, newRole);
    }
}
