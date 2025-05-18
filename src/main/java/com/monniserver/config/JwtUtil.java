package com.monniserver.config;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {
    private final JwtProperties jwtProperties;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    public String generateJwtToken(UUID userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getExpireLength());

        return Jwts.builder()
                .claim("sub", userId.toString())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, getSignKey())
                .compact();
    }

    public boolean isTokenValid(String jwtToken) {
        try {
            Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(jwtToken);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public UUID getUserIdFromToken(String jwtToken) {
        String userIdString = Jwts.parser().verifyWith(getSignKey()).build()
                .parseSignedClaims(jwtToken)
                .getPayload()
                .getSubject();
        return UUID.fromString(userIdString);
    }
}
