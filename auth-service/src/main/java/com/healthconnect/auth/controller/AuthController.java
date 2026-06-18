package com.healthconnect.auth.controller;

import com.healthconnect.auth.dto.LoginRequest;
import com.healthconnect.auth.dto.RegisterRequest;
import com.healthconnect.auth.dto.TokenResponse;
import com.healthconnect.auth.service.AuthService;
import com.healthconnect.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<TokenResponse>> register(@Valid @RequestBody RegisterRequest req) {
        TokenResponse tokens = authService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Registration successful", tokens));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest req) {
        TokenResponse tokens = authService.login(req);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", tokens));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@RequestParam String refreshToken) {
        TokenResponse tokens = authService.refresh(refreshToken);
        return ResponseEntity.ok(ApiResponse.ok("Token refreshed", tokens));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("auth-service UP");
    }
}
