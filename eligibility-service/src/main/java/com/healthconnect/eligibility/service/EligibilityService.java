package com.healthconnect.eligibility.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthconnect.common.exception.HealthConnectException;
import com.healthconnect.eligibility.dto.request.EligibilityInquiryDto;
import com.healthconnect.eligibility.dto.response.EligibilityResponseDto;
import com.healthconnect.eligibility.entity.EligibilityRequest;
import com.healthconnect.eligibility.entity.EligibilityStatus;
import com.healthconnect.eligibility.kafka.EligibilityEventProducer;
import com.healthconnect.eligibility.repository.EligibilityRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class EligibilityService {

    private static final DateTimeFormatter DOB_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final EligibilityRequestRepository repository;
    private final PayerGatewayClient payerGatewayClient;
    private final EligibilityEventProducer eventProducer;
    private final ObjectMapper objectMapper;

    /**
     * Submits a 270 eligibility inquiry.
     *
     * Flow:
     *  1. Reject duplicate controlNumbers (idempotency guard)
     *  2. Persist the request with status PENDING
     *  3. Forward to payer gateway (270 → 271 round-trip)
     *  4. Update DB with response + final status
     *  5. Publish Kafka event (fire-and-forget, does not affect HTTP response)
     *
     * Redis cache: keyed on tradingPartnerServiceId + memberId.
     * TTL is set to 15 minutes in RedisConfig — eligibility is real-time
     * sensitive so we don't cache aggressively.
     */
    @Transactional
    public EligibilityResponseDto submitInquiry(EligibilityInquiryDto request) {
        String controlNumber = request.getControlNumber();

        if (repository.existsByControlNumber(controlNumber)) {
            throw new HealthConnectException(
                    "Duplicate controlNumber: " + controlNumber,
                    HttpStatus.CONFLICT, "DUPLICATE_CONTROL_NUMBER");
        }

        // Persist request — capture everything before calling out
        EligibilityRequest entity = buildEntity(request);
        entity = repository.save(entity);
        log.info("Eligibility inquiry saved id={} controlNumber={}", entity.getId(), controlNumber);

        // Call payer gateway
        EligibilityResponseDto response;
        try {
            response = payerGatewayClient.submitInquiry(request);
            entity.setStatus(resolveStatus(response));
            entity.setResponsePayload(objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            log.error("Payer gateway call failed for controlNumber={}: {}", controlNumber, e.getMessage());
            entity.setStatus(EligibilityStatus.ERROR);
            entity.setErrorMessage(e.getMessage());
            repository.save(entity);
            throw new HealthConnectException(
                    "Payer gateway unavailable. Please retry.",
                    HttpStatus.SERVICE_UNAVAILABLE, "GATEWAY_ERROR");
        }

        repository.save(entity);

        // Kafka — fire-and-forget, failure must not roll back the transaction
        eventProducer.publishEligibilityChecked(
                controlNumber,
                request.getSubscriber().getMemberId(),
                request.getTradingPartnerServiceId(),
                entity.getStatus().name()
        );

        return response;
    }

    /**
     * Re-query by controlNumber — served from DB, never hits the payer.
     * Used by front-end polling for async status checks.
     */
    @Cacheable(value = "eligibility", key = "#controlNumber")
    @Transactional(readOnly = true)
    public EligibilityResponseDto getByControlNumber(String controlNumber) {
        EligibilityRequest entity = repository.findByControlNumber(controlNumber)
                .orElseThrow(() -> new HealthConnectException(
                        "No eligibility record for controlNumber: " + controlNumber,
                        HttpStatus.NOT_FOUND, "ELIGIBILITY_NOT_FOUND"));

        if (entity.getResponsePayload() == null) {
            // Still pending or errored — return status-only response
            EligibilityResponseDto partial = new EligibilityResponseDto();
            partial.setControlNumber(controlNumber);
            partial.setStatus(entity.getStatus().name());
            return partial;
        }

        try {
            return objectMapper.readValue(entity.getResponsePayload(), EligibilityResponseDto.class);
        } catch (JsonProcessingException e) {
            log.error("Could not deserialise response for controlNumber={}", controlNumber, e);
            throw new HealthConnectException(
                    "Error reading stored response", HttpStatus.INTERNAL_SERVER_ERROR, "PARSE_ERROR");
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private EligibilityRequest buildEntity(EligibilityInquiryDto req) {
        String requestJson;
        try {
            requestJson = objectMapper.writeValueAsString(req);
        } catch (JsonProcessingException e) {
            requestJson = "{}";
        }

        EligibilityRequest.EligibilityRequestBuilder builder = EligibilityRequest.builder()
                .controlNumber(req.getControlNumber())
                .tradingPartnerServiceId(req.getTradingPartnerServiceId())
                .tradingPartnerName(req.getTradingPartnerName())
                .requestPayload(requestJson)
                .status(EligibilityStatus.PENDING);

        if (req.getSubscriber() != null) {
            builder.memberId(req.getSubscriber().getMemberId())
                   .memberFirstName(req.getSubscriber().getFirstName())
                   .memberLastName(req.getSubscriber().getLastName())
                   .memberDob(parseDob(req.getSubscriber().getDateOfBirth()));
        }

        if (req.getProvider() != null) {
            builder.providerNpi(req.getProvider().getNpi())
                   .providerOrgName(req.getProvider().getOrganizationName());
        }

        if (req.getEncounter() != null
                && req.getEncounter().getBeginningDateOfService() != null) {
            builder.serviceDate(parseDob(req.getEncounter().getBeginningDateOfService()));
        }

        return builder.build();
    }

    private EligibilityStatus resolveStatus(EligibilityResponseDto response) {
        if (response.getErrors() != null && !response.getErrors().isEmpty()) {
            return EligibilityStatus.ERROR;
        }
        if (response.getPlanStatus() == null || response.getPlanStatus().isEmpty()) {
            return EligibilityStatus.PENDING;
        }
        // statusCode "1" = Active, "6" = Inactive per X12 codes
        String code = response.getPlanStatus().get(0).getStatusCode();
        return "1".equals(code) ? EligibilityStatus.ACTIVE : EligibilityStatus.INACTIVE;
    }

    private LocalDate parseDob(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(raw, DOB_FORMATTER);
        } catch (DateTimeParseException e) {
            log.warn("Could not parse date '{}', storing as null", raw);
            return null;
        }
    }
}
