package com.example.authsystem.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@FieldDefaults(level = PRIVATE)
public class JWTUtil {
    @Value("${jwt.secret}")
    String SECRET_KEY;

    @Value("${jwt.access-token-ms}")
    Long ACCESS_TOKEN_MS;

    @Value("${jwt.refresh-token-ms}")
    Long REFRESH_TOKEN_MS;

    private Key key() {
        return Keys.hmacShaKeyFor(Objects.requireNonNull(SECRET_KEY).getBytes());
    }

    public String generateAccessToken(String username, Map<String, Object> claims) {
        JwtBuilder builder = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .signWith(key(), HS256)
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_MS));

        if (claims != null && !claims.isEmpty())
            builder.addClaims(claims);

        return builder.compact();
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .signWith(key(), HS256)
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_MS))
                .compact();
    }

    public boolean validate(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT expired: {}", e.getMessage());
        } catch (JwtException e) {
            log.warn("JWT validation failed: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while validating JWT", e);
        }
        return false;
    }

    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody();
        } catch (JwtException e) {
            log.warn("Cannot extract claims from token: {}", e.getMessage());
            return null;
        }
    }

    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        return claims != null ? claims.getSubject() : null;
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        if (claims == null) return Collections.emptyList();

        Object rolesObj = claims.get("roles");
        if (rolesObj == null) return Collections.emptyList();

        if (rolesObj instanceof Collection) {
            return ((Collection<?>) rolesObj).stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
        } else {
            return Arrays.stream(rolesObj.toString().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }
    }
}