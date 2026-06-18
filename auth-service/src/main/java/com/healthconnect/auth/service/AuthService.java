package com.healthconnect.auth.service;

import com.healthconnect.auth.dto.LoginRequest;
import com.healthconnect.auth.dto.RegisterRequest;
import com.healthconnect.auth.dto.TokenResponse;
import com.healthconnect.auth.entity.RefreshToken;
import com.healthconnect.auth.entity.User;
import com.healthconnect.auth.repository.RefreshTokenRepository;
import com.healthconnect.auth.repository.UserRepository;
import com.healthconnect.common.exception.HealthConnectException;
import com.healthconnect.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Value("${jwt.access-token-ttl-ms:900000}")
    private long accessTokenTtlMs;

    @Value("${jwt.refresh-token-ttl-ms:604800000}")
    private long refreshTokenTtlMs;

    // ── Registration ──────────────────────────────────────────────────────────

    @Transactional
    public TokenResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new HealthConnectException(
                    "Email already registered: " + req.getEmail(),
                    HttpStatus.CONFLICT, "EMAIL_TAKEN");
        }
        Set<String> roles = (req.getRoles() == null || req.getRoles().isEmpty())
                ? Collections.singleton("ROLE_PROVIDER")
                : req.getRoles();

        User user = User.builder()
                .fullName(req.getFullName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .roles(roles)
                .build();
        userRepository.save(user);
        log.info("Registered new user: {}", user.getEmail());
        return buildTokenResponse(user);
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    @Transactional
    public TokenResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new HealthConnectException(
                        "User not found", HttpStatus.NOT_FOUND, "USER_NOT_FOUND"));

        // Revoke all existing refresh tokens for this user (single active session)
        refreshTokenRepository.revokeAllByUser(user);
        return buildTokenResponse(user);
    }

    // ── Token refresh ─────────────────────────────────────────────────────────

    @Transactional
    public TokenResponse refresh(String refreshToken) {
        RefreshToken stored = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new HealthConnectException(
                        "Refresh token not found", HttpStatus.UNAUTHORIZED, "INVALID_REFRESH_TOKEN"));

        if (stored.isRevoked() || stored.getExpiresAt().isBefore(Instant.now())) {
            throw new HealthConnectException(
                    "Refresh token expired or revoked", HttpStatus.UNAUTHORIZED, "REFRESH_TOKEN_EXPIRED");
        }

        stored.setRevoked(true);   // rotate
        User user = stored.getUser();
        return buildTokenResponse(user);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private TokenResponse buildTokenResponse(User user) {
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("roles", user.getRoles());
        String access  = jwtUtil.generateAccessToken(user.getId(), claims);
        String refresh = jwtUtil.generateRefreshToken(user.getId());

        RefreshToken rt = RefreshToken.builder()
                .token(refresh)
                .user(user)
                .expiresAt(Instant.now().plusMillis(refreshTokenTtlMs))
                .build();
        refreshTokenRepository.save(rt);

        return TokenResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .tokenType("Bearer")
                .expiresIn(accessTokenTtlMs / 1000)
                .userId(user.getId())
                .build();
    }
}
