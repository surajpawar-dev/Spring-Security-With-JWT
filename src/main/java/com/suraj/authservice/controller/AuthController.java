package com.suraj.authservice.controller;

import com.suraj.authservice.dto.ApiResponse;
import com.suraj.authservice.dto.LoginRequest;
import com.suraj.authservice.dto.RegisterRequest;
import com.suraj.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication API for user registration and login")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Registers a new user with username, email, phone, and password")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest request) {
        String message = authService.register(request);
        ApiResponse<String> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                true,
                "User Registration Successful",
                message
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/login")
    @Operation(summary = "Login a user", description = "Authenticates a user with username and password and returns a JWT token")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody LoginRequest request) {
        String authData = authService.login(request);
        ApiResponse<String> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                true,
                "Authentication Successful",
                authData
        );
        return ResponseEntity.ok(response);
    }
}