package com.ha.dxl.security;

import com.ha.dxl.config.JwtConfig;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * Provides JWT token generation, validation, and claim extraction.
 * Tokens use HS256 signing with a secret from environment variable JWT_SECRET.
 * @see A-02, US-001
 */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final JwtConfig jwtConfig;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a short-lived access token for the given user.
     */
    public String generateAccessToken(UserDetails userDetails) {
        return buildToken(userDetails, jwtConfig.getAccessTokenExpiryMs(), Map.of("type", "access"));
    }

    /**
     * Generates a long-lived refresh token for the given user.
     */
    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(userDetails, jwtConfig.getRefreshTokenExpiryMs(), Map.of("type", "refresh"));
    }

    private String buildToken(UserDetails userDetails, long expiryMs, Map<String, Object> extraClaims) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiryMs);
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claims(extraClaims)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Validates a JWT token and returns true if valid.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired");
        } catch (JwtException e) {
            log.warn("JWT token invalid: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Extracts the username (subject) from a JWT token.
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Extracts the token type claim ("access" or "refresh").
     */
    public String getTokenType(String token) {
        return (String) Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("type");
    }
}
