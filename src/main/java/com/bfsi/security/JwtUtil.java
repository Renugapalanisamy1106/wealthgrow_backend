package com.bfsi.security;

import java.security.Key;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Utility for creating and validating JWT tokens.
 * Token carries the userId (subject) and the user's role.
 */
@Component
public class JwtUtil {

    // Secret + expiry are read from application.properties with sane defaults.
    @Value("${app.jwt.secret:bfsi-mutual-funds-super-secret-key-change-me-32bytes-min}")
    private String secret;

    // Default: 24 hours (in milliseconds)
    @Value("${app.jwt.expiration-ms:86400000}")
    private long expirationMs;

    private Key signingKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /** Generate a signed JWT for a user. */
    public String generateToken(String userId, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(userId)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey())
                .compact();
    }

    private Claims parse(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUserId(String token) {
        return parse(token).getSubject();
    }

    public String getRole(String token) {
        Object role = parse(token).get("role");
        return role != null ? role.toString() : null;
    }

    /** Returns true if the token is well-formed, correctly signed, and not expired. */
    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
