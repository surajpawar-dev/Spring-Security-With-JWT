package com.suraj.authservice.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.suraj.authservice.dto.ApiResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * JWT Authentication filter that supports:
 * 1. JWT-only (fast, stateless)
 * 2. DB-based validation (secure)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;  // Injected ObjectMapper

    @Value("${auth.jwt.use-db}")
    private boolean useDbBasedJwt;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                              @NotNull HttpServletResponse response,
                              @NotNull FilterChain filterChain) throws ServletException, IOException {
    try {
        String token = extractTokenFromRequest(request);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        DecodedJWT decodedJWT = jwtService.validateToken(token);
        String username = decodedJWT.getSubject();

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Check if token is in blacklist (for logout functionality)
            if (isTokenBlacklisted(token)) {
                handleJwtVerificationFailure(response, new JWTVerificationException("Token has been revoked"));
                return;
            }
            
            if (useDbBasedJwt) {
                authenticateWithDatabase(request, username);
                log.debug("DB-based JWT authentication applied for user: {}", username);
            } else {
                List<GrantedAuthority> authorities = extractAuthoritiesFromToken(decodedJWT);
                authenticateWithJwtOnly(username, authorities);
                log.debug("JWT-only authentication applied for user: {}", username);
            }
        }

        filterChain.doFilter(request, response);

    } catch (JWTVerificationException e) {
        handleJwtVerificationFailure(response, e);
        // Intentionally not continuing the filter chain for security reasons
    } catch (Exception e) {
        handleUnexpectedFailure(response, e);
        // Intentionally not continuing the filter chain for security reasons
    }
}

// Method to check if a token is blacklisted (would need to be implemented)
private boolean isTokenBlacklisted(String token) {
    // Implementation would check against a blacklist storage (Redis, database, etc.)
    return false;
}

    private String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        return (authHeader != null && authHeader.startsWith("Bearer "))
                ? authHeader.substring(7)
                : null;
    }

    private void authenticateWithDatabase(HttpServletRequest request, String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void authenticateWithJwtOnly(String username, List<GrantedAuthority> authorities) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(username, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private List<GrantedAuthority> extractAuthoritiesFromToken(DecodedJWT decodedJWT) {
        String rolesString = decodedJWT.getClaim("roles").asString();

        if (rolesString == null || rolesString.isEmpty()) {
            return new ArrayList<>();
        }

        // Remove brackets from "[ROLE_USER, ROLE_ADMIN]" format : Abhi to sirf single role rkha hai but badme kam aa skta hai
        rolesString = rolesString.replaceAll("[\\[\\]]", "");


        return Arrays.stream(rolesString.split(","))
                .map(String::trim)
                .filter(role -> !role.isEmpty())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private void handleJwtVerificationFailure(HttpServletResponse response, JWTVerificationException e) throws IOException {
        log.warn("JWT verification failed: {}", e.getMessage());

        String message = switch (e.getClass().getSimpleName()) {
            case "TokenExpiredException" -> "Token has expired";
            case "SignatureVerificationException" -> "Invalid token signature";
            case "JWTDecodeException" -> "Malformed JWT token";
            default -> "Invalid JWT token";
        };

        SecurityContextHolder.clearContext();

        ApiResponse<String> apiResponse = new ApiResponse<>(
                HttpServletResponse.SC_UNAUTHORIZED,
                false,
                message,
                Map.of("jwt", e.getMessage())
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        objectMapper.writeValue(response.getOutputStream(), apiResponse);
    }


    /**
     * Handles any unexpected/unhandled exceptions that occur during filter processing.
     */

    private void handleUnexpectedFailure(HttpServletResponse response, Exception e) throws IOException {
        log.error("Unexpected error during authentication filter", e);

        SecurityContextHolder.clearContext();

        ApiResponse<String> apiResponse = new ApiResponse<>(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                false,
                "Internal authentication error",
                Map.of("error", e.getClass().getSimpleName(), "details", e.getMessage())
        );

        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType("application/json");
//        Take this Java object (apiResponse),
//        convert it to JSON, 
//        and write it directly to the HTTP response body.
        objectMapper.writeValue(response.getOutputStream(), apiResponse);
    }


}