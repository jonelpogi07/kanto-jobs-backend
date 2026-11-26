package com.kanto_jobs.kanto_jobs.security;

import com.kanto_jobs.kanto_jobs.domain.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
    @Value("${jwt.secret}") private String secret;
    @Value("${jwt.expiry-ms}") private long expiryMs;

    private SecretKey key() {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            byte[] padded = new byte[32];
            System.arraycopy(bytes, 0, padded, 0, bytes.length);
            bytes = padded;
        }
        return Keys.hmacShaKeyFor(bytes);
    }

    public String issue(User u) {
        return Jwts.builder()
            .subject(String.valueOf(u.getId()))
            .claim("email", u.getEmail())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiryMs))
            .signWith(key())
            .compact();
    }

    public Long parseSubject(String token) {
        String sub = Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload().getSubject();
        return Long.valueOf(sub);
    }

    public long getExpiryMs() { return expiryMs; }
}