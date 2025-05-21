package com.suraj.authservice.dto;

import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
public class ApiResponse<T> {

    private Instant timestamp;
    private int status;
    private boolean success;
    private String message;
    private T data;
    private Map<String, String> errors;

    // Default constructor
    public ApiResponse() {
        this.timestamp = Instant.now();
    }

    // Constructor for success responses
    public ApiResponse(int status, boolean success, String message, T data) {
        this.timestamp = Instant.now();
        this.status = status;
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Constructor for error responses
    public ApiResponse(int status, boolean success, String message, Map<String, String> errors) {
        this.timestamp = Instant.now();
        this.status = status;
        this.success = success;
        this.message = message;
        this.errors = errors;
    }
}
