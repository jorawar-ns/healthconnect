package com.healthconnect.claims.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthconnect.claims.dto.request.ClaimLineItemDto;
import com.healthconnect.claims.dto.request.SubmitClaimDto;
import com.healthconnect.claims.dto.response.ClaimLineItemResponseDto;
import com.healthconnect.claims.dto.response.ClaimResponseDto;
import com.healthconnect.claims.entity.Claim;
import com.healthconnect.claims.entity.ClaimLineItem;
import com.healthconnect.claims.entity.ClaimStatus;
import com.healthconnect.claims.kafka.ClaimEventProducer;
import com.healthconnect.claims.repository.ClaimRepository;
import com.healthconnect.common.exception.HealthConnectException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Core claims processing service.
 *
 * Sprint 3 scope: submit + retrieve claims.
 * Adjudication callback will be handled in Sprint 4 (HCEP-2050).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final ClaimEventProducer eventProducer;
    private final ObjectMapper objectMapper;

    // ── Submit ────────────────────────────────────────────────────────────────

    @Transactional
    public ClaimResponseDto submitClaim(SubmitClaimDto dto) {
        // Idempotency guard — controlNumber must be unique per payer spec
        if (claimRepository.existsByClaimNumber(dto.getControlNumber())) {
            throw new HealthConnectException(
                    "Duplicate controlNumber: " + dto.getControlNumber(),
                    HttpStatus.CONFLICT,
                    "CLAIM_DUPLICATE");
        }

        // Generate internal claim number
        String claimNumber = "HC-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();

        // Build line items
        List<ClaimLineItem> lineItems = dto.getLineItems().stream()
                .map(this::toLineItem)
                .collect(Collectors.toList());

        BigDecimal totalCharge = lineItems.stream()
                .map(li -> li.getChargeAmount() != null ? li.getChargeAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Claim claim = Claim.builder()
                .claimNumber(claimNumber)
                .controlNumber(dto.getControlNumber())
                .payerId(dto.getPayerId())
                .memberId(dto.getMemberId())
                .providerNpi(dto.getProviderNpi())
                .providerTin(dto.getProviderTin())
                .renderingProviderNpi(dto.getRenderingProviderNpi())
                .claimType(dto.getClaimType())
                .serviceFromDate(dto.getServiceFromDate())
                .serviceToDate(dto.getServiceToDate())
                .totalCharge(totalCharge)
                .status(ClaimStatus.RECEIVED)
                .submittedAt(Instant.now())
                .rawPayload(serializePayload(dto))
                .build();

        // Wire parent reference on each line item
        lineItems.forEach(li -> li.setClaim(claim));
        claim.getLineItems().addAll(lineItems);

        Claim saved = claimRepository.save(claim);
        log.info("Claim submitted claimNumber={} controlNumber={} memberId={} totalCharge={}",
                saved.getClaimNumber(), saved.getControlNumber(), saved.getMemberId(), saved.getTotalCharge());

        // Kafka — fire-and-forget
        eventProducer.publishClaimSubmitted(
                saved.getClaimNumber(), saved.getMemberId(),
                saved.getPayerId(), saved.getClaimType().name());

        return toResponseDto(saved);
    }

    // ── Query ─────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public ClaimResponseDto getByClaimNumber(String claimNumber) {
        Claim claim = claimRepository.findByClaimNumber(claimNumber)
                .orElseThrow(() -> new HealthConnectException(
                        "Claim not found: " + claimNumber,
                        HttpStatus.NOT_FOUND,
                        "CLAIM_NOT_FOUND"));
        return toResponseDto(claim);
    }

    @Transactional(readOnly = true)
    public Page<ClaimResponseDto> getByMemberId(String memberId, Pageable pageable) {
        return claimRepository.findByMemberId(memberId, pageable)
                .map(this::toResponseDto);
    }

    @Transactional(readOnly = true)
    public Page<ClaimResponseDto> getByStatus(ClaimStatus status, Pageable pageable) {
        return claimRepository.findByStatus(status, pageable)
                .map(this::toResponseDto);
    }

    // ── Adjudication callback (stub — Sprint 4) ───────────────────────────────

    /**
     * Update claim with payer adjudication result.
     * Called by the ERA service after processing 835 files.
     * Full implementation in Sprint 4 (HCEP-2050).
     */
    @Transactional
    public ClaimResponseDto applyAdjudication(String claimNumber,
                                              String payerClaimNumber,
                                              String icn,
                                              ClaimStatus finalStatus,
                                              BigDecimal paidAmount,
                                              String denialReasonCode,
                                              String denialReasonDescription) {
        Claim claim = claimRepository.findByClaimNumber(claimNumber)
                .orElseThrow(() -> new HealthConnectException(
                        "Claim not found for adjudication: " + claimNumber,
                        HttpStatus.NOT_FOUND,
                        "CLAIM_NOT_FOUND"));

        claim.setPayerClaimNumber(payerClaimNumber);
        claim.setIcn(icn);
        claim.setStatus(finalStatus);
        claim.setPaidAmount(paidAmount);
        claim.setDenialReasonCode(denialReasonCode);
        claim.setDenialReasonDescription(denialReasonDescription);
        claim.setAdjudicatedAt(Instant.now());

        Claim updated = claimRepository.save(claim);
        log.info("Claim adjudicated claimNumber={} status={} icn={}", claimNumber, finalStatus, icn);

        // Notify downstream services
        eventProducer.publishClaimAdjudicated(
                updated.getClaimNumber(), updated.getMemberId(),
                updated.getStatus().name(), updated.getIcn());

        return toResponseDto(updated);
    }

    // ── Mapping ───────────────────────────────────────────────────────────────

    private ClaimLineItem toLineItem(ClaimLineItemDto dto) {
        return ClaimLineItem.builder()
                .lineNumber(dto.getLineNumber())
                .procedureCode(dto.getProcedureCode())
                .procedureQualifier(dto.getProcedureQualifier())
                .modifier1(dto.getModifier1())
                .modifier2(dto.getModifier2())
                .diagnosisPointer(dto.getDiagnosisPointer())
                .serviceDate(dto.getServiceDate())
                .units(dto.getUnits())
                .chargeAmount(dto.getChargeAmount())
                .status(ClaimStatus.RECEIVED)
                .build();
    }

    private ClaimResponseDto toResponseDto(Claim claim) {
        ClaimResponseDto dto = new ClaimResponseDto();
        dto.setId(claim.getId());
        dto.setClaimNumber(claim.getClaimNumber());
        dto.setControlNumber(claim.getControlNumber());
        dto.setPayerId(claim.getPayerId());
        dto.setMemberId(claim.getMemberId());
        dto.setProviderNpi(claim.getProviderNpi());
        dto.setProviderTin(claim.getProviderTin());
        dto.setRenderingProviderNpi(claim.getRenderingProviderNpi());
        dto.setClaimType(claim.getClaimType() != null ? claim.getClaimType().name() : null);
        dto.setServiceFromDate(claim.getServiceFromDate());
        dto.setServiceToDate(claim.getServiceToDate());
        dto.setTotalCharge(claim.getTotalCharge());
        dto.setAllowedAmount(claim.getAllowedAmount());
        dto.setPaidAmount(claim.getPaidAmount());
        dto.setPatientResponsibility(claim.getPatientResponsibility());
        dto.setStatus(claim.getStatus() != null ? claim.getStatus().name() : null);
        dto.setPayerClaimNumber(claim.getPayerClaimNumber());
        dto.setIcn(claim.getIcn());
        dto.setDenialReasonCode(claim.getDenialReasonCode());
        dto.setDenialReasonDescription(claim.getDenialReasonDescription());
        dto.setSubmittedAt(claim.getSubmittedAt());
        dto.setAdjudicatedAt(claim.getAdjudicatedAt());
        dto.setCreatedAt(claim.getCreatedAt());

        if (claim.getLineItems() != null) {
            dto.setLineItems(claim.getLineItems().stream()
                    .map(this::toLineItemDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private ClaimLineItemResponseDto toLineItemDto(ClaimLineItem li) {
        ClaimLineItemResponseDto dto = new ClaimLineItemResponseDto();
        dto.setId(li.getId());
        dto.setLineNumber(li.getLineNumber());
        dto.setProcedureCode(li.getProcedureCode());
        dto.setProcedureQualifier(li.getProcedureQualifier());
        dto.setModifier1(li.getModifier1());
        dto.setModifier2(li.getModifier2());
        dto.setDiagnosisPointer(li.getDiagnosisPointer());
        dto.setServiceDate(li.getServiceDate());
        dto.setUnits(li.getUnits());
        dto.setChargeAmount(li.getChargeAmount());
        dto.setAllowedAmount(li.getAllowedAmount());
        dto.setPaidAmount(li.getPaidAmount());
        dto.setStatus(li.getStatus() != null ? li.getStatus().name() : null);
        dto.setDenialReasonCode(li.getDenialReasonCode());
        return dto;
    }

    private String serializePayload(SubmitClaimDto dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            log.warn("Could not serialize claim payload for audit: {}", e.getMessage());
            return null;
        }
    }
}
