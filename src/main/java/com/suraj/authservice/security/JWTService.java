package com.suraj.authservice.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JWTService {
    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.expiration-seconds}") // 30 minutes default
    private long expirationSeconds;

    @Value("${jwt.issuer}")
    private String issuer;

    public String generateJwtToken(CustomUserDetails userDetails) {
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plusSeconds(expirationSeconds))
                .withIssuer(issuer)
                .withJWTId(UUID.randomUUID().toString())
                .withClaim("userId", userDetails.getUserId().toString())
                .withClaim("email", userDetails.getEmail())
                .withClaim("roles", userDetails.getAuthorities().toString())
                .sign(Algorithm.HMAC256(secretKey));
    }
    
    public DecodedJWT validateToken(String token) throws JWTVerificationException {
        // This method will automatically check if the token is valid
        // including signature verification and expiration check
        return JWT.require(Algorithm.HMAC256(secretKey))
                .withIssuer(issuer)
                .build()
                .verify(token);
    }

}