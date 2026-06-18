package com.healthconnect.eligibility.controller;

import com.healthconnect.common.dto.ApiResponse;
import com.healthconnect.eligibility.dto.request.EligibilityInquiryDto;
import com.healthconnect.eligibility.dto.response.EligibilityResponseDto;
import com.healthconnect.eligibility.service.EligibilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Eligibility endpoints — mirrors the EligibilityV3 OpenAPI spec paths.
 *
 * External path (through gateway): /api/eligibility/medicalnetwork/eligibility/v3/
 * Internal path (this service):    /medicalnetwork/eligibility/v3/
 */
@Slf4j
@RestController
@RequestMapping("/medicalnetwork/eligibility/v3")
@RequiredArgsConstructor
public class EligibilityController {

    private final EligibilityService eligibilityService;

    @GetMapping("/healthcheck")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.ok("eligibility-service is UP"));
    }

    /**
     * POST / — submit a 270 eligibility inquiry.
     * Matches spec: POST /medicalnetwork/eligibility/v3/
     */
    @PostMapping("/")
    public ResponseEntity<ApiResponse<EligibilityResponseDto>> submitInquiry(
            @Valid @RequestBody EligibilityInquiryDto request) {

        log.info("Received eligibility inquiry controlNumber={} tradingPartner={}",
                request.getControlNumber(), request.getTradingPartnerServiceId());

        EligibilityResponseDto response = eligibilityService.submitInquiry(request);
        return ResponseEntity.ok(ApiResponse.ok("Eligibility inquiry processed", response));
    }

    /**
     * GET /{controlNumber} — re-query a previously submitted transaction.
     * Not in the original spec but added for internal tooling and UI polling.
     */
    @GetMapping("/{controlNumber}")
    public ResponseEntity<ApiResponse<EligibilityResponseDto>> getByControlNumber(
            @PathVariable String controlNumber) {

        EligibilityResponseDto response = eligibilityService.getByControlNumber(controlNumber);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
