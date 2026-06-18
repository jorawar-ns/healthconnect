package com.healthconnect.claims.controller;

import com.healthconnect.claims.dto.request.SubmitClaimDto;
import com.healthconnect.claims.dto.response.ClaimResponseDto;
import com.healthconnect.claims.entity.ClaimStatus;
import com.healthconnect.claims.service.ClaimService;
import com.healthconnect.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Claims REST API.
 *
 * External path (via gateway): /api/claims/**
 * Internal path:               /claims/**
 */
@Slf4j
@RestController
@RequestMapping("/claims")
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService claimService;

    @GetMapping("/healthcheck")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.ok("claims-service is UP"));
    }

    /**
     * POST /claims/submit
     * Submit a new claim. Returns the created claim with an internal claimNumber.
     */
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<ClaimResponseDto>> submitClaim(
            @Valid @RequestBody SubmitClaimDto dto) {

        log.info("Claim submit request: controlNumber={} memberId={} payerId={}",
                dto.getControlNumber(), dto.getMemberId(), dto.getPayerId());

        ClaimResponseDto response = claimService.submitClaim(dto);
        return ResponseEntity.ok(ApiResponse.ok("Claim submitted successfully", response));
    }

    /**
     * GET /claims/{claimNumber}
     * Retrieve a claim by its internal HealthConnect claim number.
     */
    @GetMapping("/{claimNumber}")
    public ResponseEntity<ApiResponse<ClaimResponseDto>> getByClaimNumber(
            @PathVariable String claimNumber) {

        ClaimResponseDto response = claimService.getByClaimNumber(claimNumber);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /**
     * GET /claims/member/{memberId}
     * List all claims for a member. Supports pagination.
     */
    @GetMapping("/member/{memberId}")
    public ResponseEntity<ApiResponse<Page<ClaimResponseDto>>> getByMember(
            @PathVariable String memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("submittedAt").descending());
        Page<ClaimResponseDto> result = claimService.getByMemberId(memberId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    /**
     * GET /claims?status=RECEIVED&page=0&size=20
     * List claims by status (ops/admin use).
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ClaimResponseDto>>> getByStatus(
            @RequestParam(required = false, defaultValue = "RECEIVED") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        ClaimStatus claimStatus;
        try {
            claimStatus = ClaimStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            claimStatus = ClaimStatus.RECEIVED;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("submittedAt").descending());
        Page<ClaimResponseDto> result = claimService.getByStatus(claimStatus, pageable);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}
