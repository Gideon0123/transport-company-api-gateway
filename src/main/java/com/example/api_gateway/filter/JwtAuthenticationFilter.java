package com.example.api_gateway.filter;

import com.example.api_gateway.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtService jwtService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             WebFilterChain chain) {

        String path = exchange.getRequest()
                .getURI()
                .getPath();

        // PUBLIC ROUTES
        if (path.contains("/api/v1/auth")) {
            return chain.filter(exchange);
        }

        String token = null;

        // AUTH HEADER
        String authHeader = exchange
                .getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null &&
                authHeader.startsWith("Bearer ")) {

            token = authHeader.substring(7);
        }

        // COOKIE FALLBACK
        if (token == null) {

            HttpCookie cookie = exchange
                    .getRequest()
                    .getCookies()
                    .getFirst("accessToken");

            if (cookie != null) {
                token = cookie.getValue();
            }
        }

        // NO TOKEN
        if (token == null) {
            return chain.filter(exchange);
        }

        try {

            // BLACKLIST CHECK
            String finalToken = token;
            return jwtService.isTokenBlacklisted(token)
                    .flatMap(isBlacklisted -> {

                        if (Boolean.TRUE.equals(isBlacklisted)) {

                            exchange.getResponse()
                                    .setStatusCode(HttpStatus.UNAUTHORIZED);

                            return exchange.getResponse().setComplete();
                        }

                        String username =
                                jwtService.extractUsername(finalToken);

                        String role =
                                jwtService.extractClaim(
                                        finalToken,
                                        claims -> claims.get("role", String.class)
                                );

                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(
                                        username,
                                        null,
                                        List.of(
                                                new SimpleGrantedAuthority(
                                                        "ROLE_" + role
                                                )
                                        )
                                );

                        ServerWebExchange mutatedExchange = exchange.mutate()
                                .request(
                                        exchange.getRequest()
                                                .mutate()
                                                .header("X-Authenticated-User", username)
                                                .build()
                                )
                                .build();

                        return chain.filter(mutatedExchange)
                                .contextWrite(
                                        ReactiveSecurityContextHolder
                                                .withAuthentication(auth)
                                );
                    });

        } catch (Exception e) {

            exchange.getResponse()
                    .setStatusCode(HttpStatus.UNAUTHORIZED);

            return exchange.getResponse().setComplete();
        }
    }
}