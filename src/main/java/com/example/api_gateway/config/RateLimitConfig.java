package com.example.api_gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimitConfig {

    @Bean
    public KeyResolver userKeyResolver() {

        return exchange -> {

            String user = exchange.getRequest()
                    .getHeaders()
                    .getFirst("X-Authenticated-User");

            if (user == null || user.isBlank()) {
                user = "anonymous";
            }

            return Mono.just(user);
        };
    }

    @Bean
    @Primary
    public KeyResolver ipKeyResolver() {

        return exchange -> {

            String ip = exchange.getRequest()
                    .getRemoteAddress()
                    .getAddress()
                    .getHostAddress();

            return Mono.just(ip);
        };
    }
}