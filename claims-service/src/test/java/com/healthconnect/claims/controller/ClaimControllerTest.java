package com.healthconnect.claims.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.healthconnect.claims.dto.request.ClaimLineItemDto;
import com.healthconnect.claims.dto.request.SubmitClaimDto;
import com.healthconnect.claims.dto.response.ClaimResponseDto;
import com.healthconnect.claims.entity.ClaimType;
import com.healthconnect.claims.service.ClaimService;
import com.healthconnect.common.exception.HealthConnectException;
import com.healthconnect.common.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller slice tests for ClaimController.
 *
 * Uses @WebMvcTest — only the web layer is loaded (no DB, no Kafka).
 * Security auto-configuration is excluded; gateway JWT validation is not
 * the responsibility of this service (it trusts headers injected by the gateway).
 */
@WebMvcTest(
        value = ClaimController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = WebSecurityConfigurerAdapter.class
        )
)
@Import(GlobalExceptionHandler.class)  // common module — not auto-scanned by @WebMvcTest
class ClaimControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClaimService claimService;

    private ObjectMapper objectMapper;
    private ClaimResponseDto sampleResponse;
    private SubmitClaimDto validRequest;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        sampleResponse = new ClaimResponseDto();
        sampleResponse.setId(1L);
        sampleResponse.setClaimNumber("HC-ABCDEF123456");
        sampleResponse.setControlNumber("CLM-20250115-0001");
        sampleResponse.setPayerId("UHC001");
        sampleResponse.setMemberId("MBR-100001");
        sampleResponse.setProviderNpi("1234567890");
        sampleResponse.setClaimType("PROFESSIONAL");
        sampleResponse.setStatus("RECEIVED");
        sampleResponse.setTotalCharge(new BigDecimal("150.00"));
        sampleResponse.setSubmittedAt(Instant.parse("2025-01-15T10:00:00Z"));
        sampleResponse.setLineItems(new ArrayList<>());

        ClaimLineItemDto lineItem = new ClaimLineItemDto();
        lineItem.setLineNumber(1);
        lineItem.setProcedureCode("99213");
        lineItem.setProcedureQualifier("HC");
        lineItem.setServiceDate("20250115");
        lineItem.setUnits(new BigDecimal("1.00"));
        lineItem.setChargeAmount(new BigDecimal("150.00"));

        validRequest = new SubmitClaimDto();
        validRequest.setControlNumber("CLM-20250115-0001");
        validRequest.setPayerId("UHC001");
        validRequest.setMemberId("MBR-100001");
        validRequest.setProviderNpi("1234567890");
        validRequest.setClaimType(ClaimType.PROFESSIONAL);
        validRequest.setServiceFromDate("20250115");
        validRequest.setServiceToDate("20250115");
        validRequest.setLineItems(Collections.singletonList(lineItem));
    }

    // ── GET /claims/healthcheck ───────────────────────────────────────────────

    @Test
    @DisplayName("GET /claims/healthcheck: returns 200 OK")
    void healthCheck_returns200() throws Exception {
        mockMvc.perform(get("/claims/healthcheck"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", is("claims-service is UP")));
    }

    // ── POST /claims/submit ───────────────────────────────────────────────────

    @Test
    @DisplayName("POST /claims/submit: returns 200 with claim number on valid request")
    void submitClaim_validRequest_returns200() throws Exception {
        when(claimService.submitClaim(any(SubmitClaimDto.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/claims/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.claimNumber", is("HC-ABCDEF123456")))
                .andExpect(jsonPath("$.data.status", is("RECEIVED")))
                .andExpect(jsonPath("$.data.totalCharge", is(150.00)));
    }

    @Test
    @DisplayName("POST /claims/submit: returns 422 when required fields are missing")
    void submitClaim_missingRequiredFields_returns422() throws Exception {
        SubmitClaimDto invalid = new SubmitClaimDto(); // no fields set

        mockMvc.perform(post("/claims/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isUnprocessableEntity())  // GlobalExceptionHandler maps validation → 422
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.errorCode", is("VALIDATION_ERROR")));
    }

    @Test
    @DisplayName("POST /claims/submit: returns 422 when lineItems list is empty")
    void submitClaim_emptyLineItems_returns422() throws Exception {
        validRequest.setLineItems(Collections.emptyList());

        mockMvc.perform(post("/claims/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("POST /claims/submit: returns 409 when controlNumber is duplicate")
    void submitClaim_duplicate_returns409() throws Exception {
        when(claimService.submitClaim(any(SubmitClaimDto.class)))
                .thenThrow(new HealthConnectException(
                        "Duplicate controlNumber", HttpStatus.CONFLICT, "CLAIM_DUPLICATE"));

        mockMvc.perform(post("/claims/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isConflict());
    }

    // ── GET /claims/{claimNumber} ─────────────────────────────────────────────

    @Test
    @DisplayName("GET /claims/{claimNumber}: returns 200 with full claim detail")
    void getByClaimNumber_found_returns200() throws Exception {
        when(claimService.getByClaimNumber("HC-ABCDEF123456")).thenReturn(sampleResponse);

        mockMvc.perform(get("/claims/HC-ABCDEF123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.claimNumber", is("HC-ABCDEF123456")))
                .andExpect(jsonPath("$.data.memberId", is("MBR-100001")))
                .andExpect(jsonPath("$.data.claimType", is("PROFESSIONAL")));
    }

    @Test
    @DisplayName("GET /claims/{claimNumber}: returns 404 when claim not found")
    void getByClaimNumber_notFound_returns404() throws Exception {
        when(claimService.getByClaimNumber("HC-DOESNOTEXIST"))
                .thenThrow(new HealthConnectException(
                        "Claim not found", HttpStatus.NOT_FOUND, "CLAIM_NOT_FOUND"));

        mockMvc.perform(get("/claims/HC-DOESNOTEXIST"))
                .andExpect(status().isNotFound());
    }

    // ── GET /claims/member/{memberId} ─────────────────────────────────────────

    @Test
    @DisplayName("GET /claims/member/{memberId}: returns paginated claims")
    void getByMember_returnsPaginatedResponse() throws Exception {
        PageRequest pageable = PageRequest.of(0, 20);
        Page<ClaimResponseDto> page = new PageImpl<>(
                Collections.singletonList(sampleResponse), pageable, 1);
        when(claimService.getByMemberId(eq("MBR-100001"), any())).thenReturn(page);

        mockMvc.perform(get("/claims/member/MBR-100001")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements", is(1)))
                .andExpect(jsonPath("$.data.content[0].memberId", is("MBR-100001")));
    }

    @Test
    @DisplayName("GET /claims/member/{memberId}: returns empty page when no claims found")
    void getByMember_emptyResult_returns200WithEmptyPage() throws Exception {
        PageRequest pageable = PageRequest.of(0, 20);
        when(claimService.getByMemberId(eq("MBR-UNKNOWN"), any()))
                .thenReturn(Page.empty(pageable));

        mockMvc.perform(get("/claims/member/MBR-UNKNOWN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements", is(0)));
    }

    // ── GET /claims?status=... ────────────────────────────────────────────────

    @Test
    @DisplayName("GET /claims?status=RECEIVED: returns paginated claims by status")
    void getByStatus_validStatus_returnsClaims() throws Exception {
        PageRequest pageable = PageRequest.of(0, 20);
        Page<ClaimResponseDto> page = new PageImpl<>(
                Collections.singletonList(sampleResponse), pageable, 1);
        when(claimService.getByStatus(any(), any())).thenReturn(page);

        mockMvc.perform(get("/claims")
                        .param("status", "RECEIVED")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements", is(1)))
                .andExpect(jsonPath("$.data.content[0].status", is("RECEIVED")));
    }

    @Test
    @DisplayName("GET /claims?status=INVALID: falls back to RECEIVED status gracefully")
    void getByStatus_invalidStatus_fallsBackToReceived() throws Exception {
        PageRequest pageable = PageRequest.of(0, 20);
        Page<ClaimResponseDto> page = new PageImpl<>(
                Collections.singletonList(sampleResponse), pageable, 1);
        when(claimService.getByStatus(any(), any())).thenReturn(page);

        // Invalid status enum — controller catches and defaults to RECEIVED (no 400)
        mockMvc.perform(get("/claims")
                        .param("status", "BOGUS_STATUS"))
                .andExpect(status().isOk());
    }
}
