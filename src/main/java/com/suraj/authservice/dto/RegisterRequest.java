package com.suraj.authservice.dto;

import com.suraj.authservice.entity.Role;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link com.suraj.authservice.entity.User}
 */
public record RegisterRequest(
        UUID id,
        String username,
        String email,
        String phoneNumber,
        String password
       ) implements Serializable {
}