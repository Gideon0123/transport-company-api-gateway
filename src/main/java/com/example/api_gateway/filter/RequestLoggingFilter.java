package com.example.api_gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain
    ) {

        long startTime = System.currentTimeMillis();

        String path = exchange.getRequest()
                .getURI()
                .getPath();

        String method = exchange.getRequest()
                .getMethod()
                .name();

        String ip = exchange.getRequest()
                .getRemoteAddress()
                .getAddress()
                .getHostAddress();

        log.info(
                "Incoming Request -> Method: {}, Path: {}, IP: {}",
                method,
                path,
                ip
        );

        return chain.filter(exchange)
                .then(
                        Mono.fromRunnable(() -> {

                            long duration =
                                    System.currentTimeMillis() - startTime;

                            int status =
                                    exchange.getResponse()
                                            .getStatusCode()
                                            .value();

                            log.info(
                                    "Response -> Status: {}, Duration: {}ms",
                                    status,
                                    duration
                            );
                        })
                );
    }

    @Override
    public int getOrder() {
        return -1;
    }
}