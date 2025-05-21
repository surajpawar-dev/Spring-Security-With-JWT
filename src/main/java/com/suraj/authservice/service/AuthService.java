package com.suraj.authservice.service;

import com.suraj.authservice.dto.LoginRequest;
import com.suraj.authservice.dto.RegisterRequest;

import java.util.Map;

public interface AuthService {
    /**
     * Registers a new user in the system
     *
     * @param request The registration details
     * @return User registration confirmation message
     */
    String register(RegisterRequest request);
    
    /**
     * Authenticates a user and generates a JWT token
     * 
     * @param request The login credentials
     * @return Authentication data including JWT token and user details
     */
    String login(LoginRequest request);
}