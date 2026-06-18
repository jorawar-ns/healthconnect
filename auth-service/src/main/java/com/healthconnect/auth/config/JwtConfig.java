package com.healthconnect.auth.config;

import com.healthconnect.common.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-ttl-ms:900000}")
    private long accessTokenTtlMs;

    @Value("${jwt.refresh-token-ttl-ms:604800000}")
    private long refreshTokenTtlMs;

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(secret, accessTokenTtlMs, refreshTokenTtlMs);
    }
}
