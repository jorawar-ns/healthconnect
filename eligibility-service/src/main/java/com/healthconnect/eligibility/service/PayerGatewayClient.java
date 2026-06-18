package com.healthconnect.eligibility.service;

import com.healthconnect.eligibility.dto.request.EligibilityInquiryDto;
import com.healthconnect.eligibility.dto.response.EligibilityResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * HTTP client for the upstream payer clearinghouse gateway.
 *
 * Uses RestTemplate — WebClient was evaluated during the 2023 tech radar
 * review but was not approved for adoption in this service due to the
 * reactive programming model requiring a larger team training investment.
 * Scheduled for Sprint 12 (HCEP-2201).
 *
 * The gateway URL is environment-specific (sandbox / prod) and injected
 * from application properties, which are backed by AWS Secrets Manager
 * in non-local environments.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PayerGatewayClient {

    private final RestTemplate restTemplate;

    @Value("${gateway.eligibility.url:https://sandbox-apigw.optum.com/medicalnetwork/eligibility/v3}")
    private String gatewayUrl;

    @Value("${gateway.eligibility.api-key:}")
    private String apiKey;

    public EligibilityResponseDto submitInquiry(EligibilityInquiryDto request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (apiKey != null && !apiKey.isEmpty()) {
            headers.set("Authorization", "Bearer " + apiKey);
        }

        HttpEntity<EligibilityInquiryDto> entity = new HttpEntity<>(request, headers);

        log.info("Calling payer gateway for controlNumber={}", request.getControlNumber());

        ResponseEntity<EligibilityResponseDto> response = restTemplate.exchange(
                gatewayUrl + "/",
                HttpMethod.POST,
                entity,
                EligibilityResponseDto.class
        );

        log.info("Gateway responded with status={} for controlNumber={}",
                response.getStatusCode(), request.getControlNumber());

        return response.getBody();
    }
}
