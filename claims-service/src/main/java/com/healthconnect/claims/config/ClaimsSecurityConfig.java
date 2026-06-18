package com.healthconnect.claims.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * Claims service is an internal service — JWT validation happens at the gateway.
 * This config simply ensures all requests that reach claims-service have already
 * been authenticated by the gateway's JwtAuthFilter.
 *
 * Actuator endpoints are open for Kubernetes liveness/readiness probes.
 */
@Configuration
@EnableWebSecurity
public class ClaimsSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
                .antMatchers("/actuator/health", "/actuator/info").permitAll()
                .antMatchers("/claims/healthcheck").permitAll()
                .anyRequest().authenticated();
        // No JWT filter here — gateway already validated the token and injected
        // X-User-Id / X-User-Roles headers before routing to this service.
    }
}
