package com.monniserver.config;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final JwtProperties jwtProperties;

    private SecretKey getSignKey(){
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    public String generateJwtToken(UUID userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getExpireLength());

        return Jwts.builder()
                .claim("sub", userId.toString())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(SignatureAlgorithm.ES256, getSignKey())
                .compact();
    }

    public boolean isTokenValid(String jwtToken) {
        try {
            Jwts.parser().verifyWith((SecretKey) getSignKey()).build();
            return true;
        } catch (JwtException e){
            return false;
        }
    }

    public UUID getUserIdFromToken(String jwtToken) {
        String userIdString = Jwts.parser().verifyWith((SecretKey) getSignKey()).build()
                .parseSignedClaims(jwtToken)
                .getPayload()
                .getSubject();
        return UUID.fromString(userIdString);
    }
}
