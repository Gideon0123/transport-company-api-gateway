package com.example.api_gateway.config;

import com.example.api_gateway.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http
    ) {

        return http

                // DISABLE CSRF
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                // DISABLE FORM LOGIN
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)

                // DISABLE BASIC AUTH
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)

                // STATELESS
                .securityContextRepository(
                        NoOpServerSecurityContextRepository.getInstance()
                )

                // CORS
                .cors(cors -> {})

                // AUTHORIZATION
                .authorizeExchange(exchange -> exchange

                        // PUBLIC ROUTES
                        .pathMatchers(
                                "/api/v1/auth/**",
                                "/oauth2/**",
                                "/login/**",
                                "/eureka/**"
                        ).permitAll()

                        // ADMIN ROUTES
                        .pathMatchers("/api/v1/admin/**")
                        .hasRole("ADMIN")

                        // STAFF ROUTES
                        .pathMatchers("/api/v1/staffs/**")
                        .hasAnyRole("ADMIN", "STAFF")

                        // EVERYTHING ELSE
                        .anyExchange()
                        .authenticated()
                )

                // JWT FILTER
                .addFilterAt(
                        jwtAuthenticationFilter,
                        SecurityWebFiltersOrder.AUTHENTICATION
                )

                .build();
    }
}