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

//    public String generateAccessToken(User user) {
//
//        String role = null;
//
//        if (user.getUserType() == UserType.STAFF && user.getStaff() != null) {
//            role = user.getStaff().getRoleType().name();
//        }
//
//        return Jwts.builder()
//                .setSubject(user.getUsername())
//                .claim("role", role)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
//                .signWith(getSignKey(), SignatureAlgorithm.HS256)
//                .compact();
//    }

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
//        return Jwts.parserBuilder()
//                .setSigningKey(getSignKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody();

        return Jwts
                .parser()
                .verifyWith((SecretKey) getSignKey())
//                .setSigningKey(getSignKey())
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

//@Service
//public class JwtService {
//
////    @Value("${jwt.secret}")
//    @Value("${jwt.secret:default-secret}")
//    private String secret;
//
//    private SecretKey getSigningKey() {
//        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
//    }
//
//    public Claims extractAllClaims(String token) {
//
//        return Jwts
//                .parser()
//                .verifyWith(getSigningKey())
//                .build()
//                .parseSignedClaims(token)
//                .getPayload();
//    }
//
//    public boolean isTokenValid(String token) {
//
//        try {
//            extractAllClaims(token);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//}