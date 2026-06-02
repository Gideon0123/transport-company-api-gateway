package com.example.api_gateway.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final ReactiveRedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.secret:default-secret}")
    private String secret;


    private Key getSignKey() {
        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isValid(String token, String username) {
        return extractUsername(token).equals(username);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith((SecretKey) getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Mono<Boolean> blacklistToken(String token) {

        return redisTemplate.opsForValue().set(
                token,
                "blacklisted",
                Duration.ofHours(1)
        );
    }

    public Mono<Boolean> isTokenBlacklisted(String token) {
        return redisTemplate.hasKey(token);
    }
}
