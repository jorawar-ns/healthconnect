package com.healthconnect.gateway.filter;

import com.healthconnect.common.constants.HealthConnectConstants;
import com.healthconnect.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * Global JWT filter — runs before every downstream request.
 * Validates the Bearer token and injects X-User-Id / X-User-Roles headers
 * so downstream services don't need to touch JWT at all.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    // Paths exempt from auth (public endpoints)
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/refresh",
            "/actuator",
            "/eureka"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(HealthConnectConstants.BEARER_PREFIX)) {
            return unauthorized(exchange);
        }

        String token = authHeader.substring(HealthConnectConstants.BEARER_PREFIX.length());
        if (!jwtUtil.isValid(token)) {
            log.warn("Invalid JWT on path: {}", path);
            return unauthorized(exchange);
        }

        // Propagate user identity to downstream services via headers
        String userId = jwtUtil.extractSubject(token);
        ServerWebExchange mutated = exchange.mutate()
                .request(r -> r.header(HealthConnectConstants.X_USER_ID_HEADER, userId))
                .build();

        return chain.filter(mutated);
    }

    @Override
    public int getOrder() {
        return -100; // run before routing filters
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
