package com.example.api_gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@Slf4j
public class CorrelationIdFilter implements GlobalFilter, Ordered {

    public static final String CORRELATION_ID = "X-Correlation-Id";

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain
    ) {

        String correlationId = exchange.getRequest()
                .getHeaders()
                .getFirst(CORRELATION_ID);


        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }

        exchange.getResponse()
                .getHeaders()
                .add(CORRELATION_ID, correlationId);

        ServerHttpRequest mutatedRequest = exchange.getRequest()
                .mutate()
                .header(CORRELATION_ID, correlationId)
                .build();


        log.info(
                "[{}] Incoming request: {}",
                correlationId,
                exchange.getRequest().getURI()
        );

        return chain.filter(exchange.mutate()
                .request(mutatedRequest)
                .build()
        );
    }

    @Override
    public int getOrder() {
        return -2;
    }
}