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

@Component
@Slf4j
public class JwtUtils {

    @Value("${myapp.jwtSecret}")
    private String secret;

    @Value("${myapp.jwtExpirationMs}")
    private int jwtExpirationMs;
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generateJwtToken(CustomUserDetails userPrincipal) {
        String tenantName;
        if ( userPrincipal.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_SYSTEM_ADMIN"))) {
            tenantName = "public";
        } else {
            try {
                tenantName = userPrincipal.getUser().getTenant().getTenantName();
            } catch (Exception e) {
                throw new RuntimeException("{}"+ e.getMessage());
            }
        }

        return Jwts.builder()
                .subject((userPrincipal.getUsername()))
                .claim("tenantName", tenantName)
                .claim("roles", userPrincipal.getAuthorities())
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key())
                .compact();
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

}
