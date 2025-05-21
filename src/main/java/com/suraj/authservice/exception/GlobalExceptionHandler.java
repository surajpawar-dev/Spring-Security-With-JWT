package com.suraj.authservice.exception;

import com.suraj.authservice.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for centralized exception handling across the application.
 * This class provides exception handling for various exceptions that might be thrown
 * during request processing and returns appropriate API responses.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles resource not found exceptions.
     *
     * @param ex The ResourceNotFoundException thrown
     * @return A ResponseEntity with error details
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());
        
        ApiResponse<String> response = new ApiResponse<>(
                HttpStatus.NOT_FOUND.value(),
                false,
                "Resource not found",
                ex.getMessage()
        );
        
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    
    /**
     * Handles authentication-related exceptions.
     *
     * @param ex The AuthenticationException thrown
     * @return A ResponseEntity with error details
     */
    @ExceptionHandler({BadCredentialsException.class, AuthenticationException.class})
    public ResponseEntity<ApiResponse<String>> handleAuthenticationException(Exception ex) {
        log.error("Authentication failed: {}", ex.getMessage());
        
        ApiResponse<String> response = new ApiResponse<>(
                HttpStatus.UNAUTHORIZED.value(),
                false,
                "Authentication failed",
                ex.getMessage()
        );
        
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
    
    /**
     * Handles authorization/access denied exceptions.
     *
     * @param ex The AccessDeniedException thrown
     * @return A ResponseEntity with error details
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<String>> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("Access denied: {}", ex.getMessage());
        
        ApiResponse<String> response = new ApiResponse<>(
                HttpStatus.FORBIDDEN.value(),
                false,
                "Access denied",
                "You don't have permission to perform this operation"
        );
        
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }
    
    /**
     * Handles role-related exceptions.
     *
     * @param ex The InvalidRoleException thrown
     * @return A ResponseEntity with error details
     */
    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidRoleException(InvalidRoleException ex) {
        log.error("Invalid role operation: {}", ex.getMessage());
        
        ApiResponse<String> response = new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                false,
                "Invalid role operation",
                ex.getMessage()
        );
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handles user already exists exceptions.
     *
     * @param ex The UserAlreadyExistsException thrown
     * @return A ResponseEntity with error details
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        log.error("User already exists: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        errors.put("user", ex.getMessage());
        
        ApiResponse<Map<String, String>> response = new ApiResponse<>(
                HttpStatus.CONFLICT.value(),
                false,
                "Registration failed. A user with the same username, email, or phone number already exists.",
                errors
        );
        
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
    
    /**
     * Handles invalid login credential exceptions.
     *
     * @param ex The InvalidCredentialsException thrown
     * @return A ResponseEntity with unauthorized status and error details
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        log.error("Invalid credentials: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        errors.put("credentials", ex.getMessage());

        ApiResponse<Map<String, String>> apiResponse = new ApiResponse<>(
                HttpStatus.UNAUTHORIZED.value(),
                false,
                "Login failed. Invalid username or password provided.",
                errors
        );

        return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
    }
    
    /**
     * Handles authentication failure scenarios.
     *
     * @param ex The AuthenticationFailedException thrown
     * @return A ResponseEntity with unauthorized status and detailed error message
     */
    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleAuthenticationFailedException(AuthenticationFailedException ex) {
        log.error("Authentication failed: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        errors.put("authentication", ex.getMessage());

        ApiResponse<Map<String, String>> apiResponse = new ApiResponse<>(
                HttpStatus.UNAUTHORIZED.value(),
                false,
                "Authentication failed. Please contact support if the problem persists.",
                errors
        );

        return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
    }
    
    /**
     * Handles validation exceptions that occur during request body validation.
     *
     * @param ex The MethodArgumentNotValidException thrown during validation
     * @return A ResponseEntity containing validation error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        log.debug("Validation failed for fields: {}", errors.keySet());

        ApiResponse<Map<String, String>> apiResponse = new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                false,
                "Validation failed for one or more fields. Please check the input and try again.",
                errors
        );

        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Fallback handler for all other exceptions.
     *
     * @param ex The Exception thrown
     * @return A ResponseEntity with error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleGlobalException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        
        ApiResponse<Map<String, String>> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                false,
                "An unexpected error occurred. Please contact support if the problem persists.",
                errors
        );
        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}