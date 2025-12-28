package com.common.starter.security;

import com.common.starter.security.abstraction.TokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT implementation of TokenProvider.
 * Provides JWT-based token generation, validation, and extraction.
 */
@Component
@Primary
public class JwtTokenProvider implements TokenProvider<Claims> {

    @Value("${jwt.secret:defaultSecretKeyWithEnoughEntropyToMakeItWork12345!}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generateToken(String subject, Map<String, Object> additionalClaims) {
        return Jwts.builder()
                .subject(subject)
                .claims(additionalClaims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            extractClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String extractSubject(String token) {
        return extractClaims(token).getSubject();
    }

    @Override
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractClaims(token).getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public long getExpirationTime() {
        return expiration;
    }

    // Legacy method for backward compatibility
    public String extractUsername(String token) {
        return extractSubject(token);
    }

    // Legacy method for backward compatibility
    public String generateToken(String username) {
        return generateToken(username, Map.of());
    }
}
