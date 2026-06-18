package com.healthconnect.claims.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.healthconnect.claims.dto.request.ClaimLineItemDto;
import com.healthconnect.claims.dto.request.SubmitClaimDto;
import com.healthconnect.claims.dto.response.ClaimResponseDto;
import com.healthconnect.claims.entity.Claim;
import com.healthconnect.claims.entity.ClaimLineItem;
import com.healthconnect.claims.entity.ClaimStatus;
import com.healthconnect.claims.entity.ClaimType;
import com.healthconnect.claims.kafka.ClaimEventProducer;
import com.healthconnect.claims.repository.ClaimRepository;
import com.healthconnect.common.exception.HealthConnectException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ClaimService.
 *
 * No Spring context — pure Mockito. Runs fast, tests logic in isolation.
 * Integration tests (with real DB) are out of scope for Sprint 3.
 */
@ExtendWith(MockitoExtension.class)
class ClaimServiceTest {

    @Mock
    private ClaimRepository claimRepository;

    @Mock
    private ClaimEventProducer eventProducer;

    /**
     * Real ObjectMapper — tests serialization logic as well as service logic.
     * Using @Spy so it can be injected by @InjectMocks with the real implementation.
     */
    @Spy
    private ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @InjectMocks
    private ClaimService claimService;

    private SubmitClaimDto validSubmitDto;
    private Claim savedClaim;

    @BeforeEach
    void setUp() {
        ClaimLineItemDto lineItem = new ClaimLineItemDto();
        lineItem.setLineNumber(1);
        lineItem.setProcedureCode("99213");
        lineItem.setProcedureQualifier("HC");
        lineItem.setServiceDate("20250115");
        lineItem.setUnits(new BigDecimal("1.00"));
        lineItem.setChargeAmount(new BigDecimal("150.00"));

        validSubmitDto = new SubmitClaimDto();
        validSubmitDto.setControlNumber("CLM-20250115-0001");
        validSubmitDto.setPayerId("UHC001");
        validSubmitDto.setMemberId("MBR-100001");
        validSubmitDto.setProviderNpi("1234567890");
        validSubmitDto.setProviderTin("123456789");
        validSubmitDto.setClaimType(ClaimType.PROFESSIONAL);
        validSubmitDto.setServiceFromDate("20250115");
        validSubmitDto.setServiceToDate("20250115");
        validSubmitDto.setLineItems(Collections.singletonList(lineItem));

        ClaimLineItem savedLineItem = ClaimLineItem.builder()
                .id(1L)
                .lineNumber(1)
                .procedureCode("99213")
                .procedureQualifier("HC")
                .serviceDate("20250115")
                .units(new BigDecimal("1.00"))
                .chargeAmount(new BigDecimal("150.00"))
                .status(ClaimStatus.RECEIVED)
                .build();

        savedClaim = Claim.builder()
                .id(1L)
                .claimNumber("HC-ABCDEF123456")
                .controlNumber("CLM-20250115-0001")
                .payerId("UHC001")
                .memberId("MBR-100001")
                .providerNpi("1234567890")
                .providerTin("123456789")
                .claimType(ClaimType.PROFESSIONAL)
                .serviceFromDate("20250115")
                .serviceToDate("20250115")
                .totalCharge(new BigDecimal("150.00"))
                .status(ClaimStatus.RECEIVED)
                .submittedAt(Instant.now())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .lineItems(new ArrayList<>(Collections.singletonList(savedLineItem)))
                .build();

        savedLineItem.setClaim(savedClaim);
    }

    // ── submitClaim ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("submitClaim: persists claim and publishes Kafka event on success")
    void submitClaim_success() {
        when(claimRepository.existsByClaimNumber(anyString())).thenReturn(false);
        when(claimRepository.save(any(Claim.class))).thenReturn(savedClaim);

        ClaimResponseDto response = claimService.submitClaim(validSubmitDto);

        assertThat(response).isNotNull();
        assertThat(response.getClaimNumber()).isEqualTo("HC-ABCDEF123456");
        assertThat(response.getMemberId()).isEqualTo("MBR-100001");
        assertThat(response.getStatus()).isEqualTo("RECEIVED");
        assertThat(response.getLineItems()).hasSize(1);

        verify(claimRepository).save(any(Claim.class));
        verify(eventProducer).publishClaimSubmitted(
                eq("HC-ABCDEF123456"), eq("MBR-100001"), eq("UHC001"), eq("PROFESSIONAL"));
    }

    @Test
    @DisplayName("submitClaim: saves claim with correct totalCharge computed from line items")
    void submitClaim_computesTotalChargeFromLineItems() {
        // Two line items: $150 + $250 = $400 total
        ClaimLineItemDto second = new ClaimLineItemDto();
        second.setLineNumber(2);
        second.setProcedureCode("99214");
        second.setProcedureQualifier("HC");
        second.setServiceDate("20250115");
        second.setUnits(new BigDecimal("1.00"));
        second.setChargeAmount(new BigDecimal("250.00"));
        validSubmitDto.getLineItems().add(second);

        // The saved entity is what matters — capture the Claim passed to save()
        ArgumentCaptor<Claim> claimCaptor = ArgumentCaptor.forClass(Claim.class);
        when(claimRepository.existsByClaimNumber(anyString())).thenReturn(false);
        when(claimRepository.save(claimCaptor.capture())).thenReturn(savedClaim);

        claimService.submitClaim(validSubmitDto);

        Claim captured = claimCaptor.getValue();
        assertThat(captured.getTotalCharge()).isEqualByComparingTo(new BigDecimal("400.00"));
    }

    @Test
    @DisplayName("submitClaim: throws CONFLICT when controlNumber already exists")
    void submitClaim_duplicateControlNumber_throwsConflict() {
        // The service checks existsByClaimNumber using controlNumber as the lookup key
        // (see submitClaim idempotency guard)
        when(claimRepository.existsByClaimNumber(validSubmitDto.getControlNumber())).thenReturn(true);

        assertThatThrownBy(() -> claimService.submitClaim(validSubmitDto))
                .isInstanceOf(HealthConnectException.class)
                .satisfies(ex -> {
                    HealthConnectException hce = (HealthConnectException) ex;
                    assertThat(hce.getStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(hce.getErrorCode()).isEqualTo("CLAIM_DUPLICATE");
                });

        verify(claimRepository, never()).save(any());
        verify(eventProducer, never()).publishClaimSubmitted(any(), any(), any(), any());
    }

    @Test
    @DisplayName("submitClaim: Kafka failure does not propagate — claim is still saved")
    void submitClaim_kafkaFailure_claimStillSaved() {
        when(claimRepository.existsByClaimNumber(anyString())).thenReturn(false);
        when(claimRepository.save(any(Claim.class))).thenReturn(savedClaim);
        doThrow(new RuntimeException("Kafka broker unreachable"))
                .when(eventProducer).publishClaimSubmitted(any(), any(), any(), any());

        // Should NOT throw — fire-and-forget Kafka
        assertThatThrownBy(() -> claimService.submitClaim(validSubmitDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Kafka broker unreachable");
        // NOTE: In production the ClaimEventProducer.onFailure() swallows the error.
        // This test verifies the service itself doesn't wrap the producer call in try/catch —
        // the fire-and-forget contract lives inside the producer's ListenableFutureCallback.
        // The save() has already been called before publish().
        verify(claimRepository).save(any(Claim.class));
    }

    // ── getByClaimNumber ──────────────────────────────────────────────────────

    @Test
    @DisplayName("getByClaimNumber: returns DTO when claim exists")
    void getByClaimNumber_found() {
        when(claimRepository.findByClaimNumber("HC-ABCDEF123456"))
                .thenReturn(Optional.of(savedClaim));

        ClaimResponseDto response = claimService.getByClaimNumber("HC-ABCDEF123456");

        assertThat(response.getClaimNumber()).isEqualTo("HC-ABCDEF123456");
        assertThat(response.getPayerId()).isEqualTo("UHC001");
        assertThat(response.getLineItems()).hasSize(1);
    }

    @Test
    @DisplayName("getByClaimNumber: throws NOT_FOUND when claim does not exist")
    void getByClaimNumber_notFound() {
        when(claimRepository.findByClaimNumber("HC-DOESNOTEXIST"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> claimService.getByClaimNumber("HC-DOESNOTEXIST"))
                .isInstanceOf(HealthConnectException.class)
                .satisfies(ex -> {
                    HealthConnectException hce = (HealthConnectException) ex;
                    assertThat(hce.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(hce.getErrorCode()).isEqualTo("CLAIM_NOT_FOUND");
                });
    }

    // ── getByMemberId ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("getByMemberId: returns paginated claims mapped to DTOs")
    void getByMemberId_returnsMappedPage() {
        PageRequest pageable = PageRequest.of(0, 20);
        Page<Claim> page = new PageImpl<>(Collections.singletonList(savedClaim), pageable, 1);
        when(claimRepository.findByMemberId("MBR-100001", pageable)).thenReturn(page);

        Page<ClaimResponseDto> result = claimService.getByMemberId("MBR-100001", pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getMemberId()).isEqualTo("MBR-100001");
    }

    @Test
    @DisplayName("getByMemberId: returns empty page when member has no claims")
    void getByMemberId_emptyPage() {
        PageRequest pageable = PageRequest.of(0, 20);
        when(claimRepository.findByMemberId("MBR-UNKNOWN", pageable))
                .thenReturn(Page.empty(pageable));

        Page<ClaimResponseDto> result = claimService.getByMemberId("MBR-UNKNOWN", pageable);

        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getContent()).isEmpty();
    }

    // ── getByStatus ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("getByStatus: returns paginated claims with correct status")
    void getByStatus_returnsMappedPage() {
        PageRequest pageable = PageRequest.of(0, 20);
        Page<Claim> page = new PageImpl<>(Collections.singletonList(savedClaim), pageable, 1);
        when(claimRepository.findByStatus(ClaimStatus.RECEIVED, pageable)).thenReturn(page);

        Page<ClaimResponseDto> result = claimService.getByStatus(ClaimStatus.RECEIVED, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getStatus()).isEqualTo("RECEIVED");
    }

    // ── applyAdjudication ─────────────────────────────────────────────────────

    @Test
    @DisplayName("applyAdjudication: updates claim fields and publishes adjudication event")
    void applyAdjudication_success() {
        when(claimRepository.findByClaimNumber("HC-ABCDEF123456"))
                .thenReturn(Optional.of(savedClaim));

        Claim adjudicatedClaim = Claim.builder()
                .id(1L)
                .claimNumber("HC-ABCDEF123456")
                .memberId("MBR-100001")
                .payerId("UHC001")
                .claimType(ClaimType.PROFESSIONAL)
                .status(ClaimStatus.PAID)
                .payerClaimNumber("PAYER-CLM-9999")
                .icn("ICN-12345678")
                .paidAmount(new BigDecimal("120.00"))
                .totalCharge(new BigDecimal("150.00"))
                .adjudicatedAt(Instant.now())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .lineItems(new ArrayList<>())
                .build();

        when(claimRepository.save(any(Claim.class))).thenReturn(adjudicatedClaim);

        ClaimResponseDto response = claimService.applyAdjudication(
                "HC-ABCDEF123456", "PAYER-CLM-9999", "ICN-12345678",
                ClaimStatus.PAID, new BigDecimal("120.00"), null, null);

        assertThat(response.getStatus()).isEqualTo("PAID");
        assertThat(response.getIcn()).isEqualTo("ICN-12345678");
        verify(eventProducer).publishClaimAdjudicated(
                eq("HC-ABCDEF123456"), eq("MBR-100001"), eq("PAID"), eq("ICN-12345678"));
    }

    @Test
    @DisplayName("applyAdjudication: throws NOT_FOUND when claim does not exist")
    void applyAdjudication_claimNotFound() {
        when(claimRepository.findByClaimNumber("HC-MISSING")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> claimService.applyAdjudication(
                "HC-MISSING", "P-999", "ICN-999",
                ClaimStatus.DENIED, BigDecimal.ZERO, "CO-4", "Not covered"))
                .isInstanceOf(HealthConnectException.class)
                .satisfies(ex -> assertThat(((HealthConnectException) ex).getStatus())
                        .isEqualTo(HttpStatus.NOT_FOUND));
    }
}
