package com.example.receipt_backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
public class JwtUtils {

    @Value("${myapp.jwtSecret}")
    private String secret;

    @Value("${myapp.jwtExpirationMs}")      // e.g., 15 * 60 * 1000 = 15 min
    private int jwtExpirationMs;

    @Value("${myapp.jwtRefreshExpirationMs}") // e.g., 7 * 24 * 60 * 60 * 1000 = 7 days
    private int jwtRefreshExpirationMs;
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generateAccessToken(CustomUserDetails userPrincipal) {
        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .claim("roles", userPrincipal.getAuthorities())
                .claim("tenantName", resolveTenantName(userPrincipal))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key())
                .compact();
    }
    public String generateRefreshToken(CustomUserDetails userPrincipal) {
        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .claim("roles", userPrincipal.getAuthorities())
                .claim("isRefreshToken", true) // helpful for distinguishing
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtRefreshExpirationMs))
                .signWith(key())
                .compact();
    }

    // Helper to get the tenant name
    private String resolveTenantName(CustomUserDetails userPrincipal) {
        if (userPrincipal.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_SYSTEM_ADMIN"))) {
            return "public";
        } else {
            return userPrincipal.getUser().getTenant().getTenantName();
        }
    }


    public String getUsernameFromJwt(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }


    public String getTenantNameFromJwtToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("tenantName", String.class);
    }
    public boolean isRefreshToken(String token) {
        Claims claims = getClaimsFromToken(token);
        Object refreshFlag = claims.get("isRefreshToken");
        return refreshFlag != null && Boolean.parseBoolean(refreshFlag.toString());
    }
    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
