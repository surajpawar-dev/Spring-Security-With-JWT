package com.suraj.authservice.service.impl;

import com.suraj.authservice.dto.LoginRequest;
import com.suraj.authservice.dto.RegisterRequest;
import com.suraj.authservice.entity.Role;
import com.suraj.authservice.entity.User;
import com.suraj.authservice.exception.AuthenticationFailedException;
import com.suraj.authservice.exception.UserAlreadyExistsException;
import com.suraj.authservice.repository.UserRepository;
import com.suraj.authservice.security.CustomUserDetails;
import com.suraj.authservice.security.JWTService;
import com.suraj.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    @Override
    public String register(RegisterRequest request) {
        // Check if a username or email already exists
        if (userRepository.existsByUsername(request.username())) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        if (request.phoneNumber() != null && userRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new UserAlreadyExistsException("Phone number already exists");
        }

        // Create a new user
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPhoneNumber(request.phoneNumber());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.ROLE_USER); // Default role

        // Save the user
        User savedUser = userRepository.save(user);
        return "User registration successful for " + savedUser.getUsername();
    }

    @Override
    public String login(LoginRequest request) {
        try {
            // Create an authentication token with the credentials from the request
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    )
            );

            // The "authenticationManager.authenticate()" method will:
            // - Validate the credentials
            // - Return a fully authenticated object if successful
            // - Throw an exception (typically ) if authentication fails "BadCredentialsException"

            // Set the authentication in the SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get the authenticated user details
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            String token = jwtService.generateJwtToken(userDetails);

            // Log the successful login
            log.info("User authenticated successfully: {}", request.username());

            // Return the token
            return token;
        } catch (BadCredentialsException e) {
            log.error("Invalid username or password for user: {}", request.username());
            throw new AuthenticationFailedException("Invalid username or password");
        } catch (Exception e) {
            log.error("Authentication error: {}", e.getMessage());
            throw new AuthenticationFailedException("Authentication failed", e);
        }
    }


}