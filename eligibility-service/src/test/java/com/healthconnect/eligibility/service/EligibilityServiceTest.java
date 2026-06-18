package com.healthconnect.eligibility.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthconnect.common.exception.HealthConnectException;
import com.healthconnect.eligibility.dto.request.EligibilityInquiryDto;
import com.healthconnect.eligibility.dto.request.SubscriberDto;
import com.healthconnect.eligibility.dto.response.EligibilityResponseDto;
import com.healthconnect.eligibility.dto.response.PlanStatusDto;
import com.healthconnect.eligibility.entity.EligibilityRequest;
import com.healthconnect.eligibility.entity.EligibilityStatus;
import com.healthconnect.eligibility.kafka.EligibilityEventProducer;
import com.healthconnect.eligibility.repository.EligibilityRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EligibilityServiceTest {

    @Mock
    private EligibilityRequestRepository repository;

    @Mock
    private PayerGatewayClient payerGatewayClient;

    @Mock
    private EligibilityEventProducer eventProducer;

    @InjectMocks
    private EligibilityService eligibilityService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        // Inject real ObjectMapper since @InjectMocks won't find it automatically
        try {
            java.lang.reflect.Field f = EligibilityService.class.getDeclaredField("objectMapper");
            f.setAccessible(true);
            f.set(eligibilityService, objectMapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ── submitInquiry ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("submitInquiry: happy path saves entity, calls gateway, publishes event")
    void submitInquiry_happyPath() {
        // arrange
        EligibilityInquiryDto request = buildRequest("CTL-001", "MEM-123");

        when(repository.existsByControlNumber("CTL-001")).thenReturn(false);
        when(repository.save(any(EligibilityRequest.class)))
                .thenAnswer(inv -> {
                    EligibilityRequest r = inv.getArgument(0);
                    r.setId(1L);
                    return r;
                });

        EligibilityResponseDto gatewayResponse = buildActiveResponse("CTL-001");
        when(payerGatewayClient.submitInquiry(request)).thenReturn(gatewayResponse);

        // act
        EligibilityResponseDto result = eligibilityService.submitInquiry(request);

        // assert
        assertThat(result).isNotNull();
        assertThat(result.getControlNumber()).isEqualTo("CTL-001");

        // entity should be saved twice: once PENDING, once with final status
        verify(repository, times(2)).save(any(EligibilityRequest.class));

        // Kafka event should fire
        verify(eventProducer).publishEligibilityChecked(
                "CTL-001", "MEM-123", "BCBS-IL", EligibilityStatus.ACTIVE.name());
    }

    @Test
    @DisplayName("submitInquiry: duplicate controlNumber throws CONFLICT")
    void submitInquiry_duplicateControlNumber_throwsConflict() {
        EligibilityInquiryDto request = buildRequest("CTL-DUP", "MEM-999");
        when(repository.existsByControlNumber("CTL-DUP")).thenReturn(true);

        assertThatThrownBy(() -> eligibilityService.submitInquiry(request))
                .isInstanceOf(HealthConnectException.class)
                .hasMessageContaining("Duplicate controlNumber");

        verify(payerGatewayClient, never()).submitInquiry(any());
        verify(eventProducer, never()).publishEligibilityChecked(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("submitInquiry: gateway failure sets status to ERROR and throws SERVICE_UNAVAILABLE")
    void submitInquiry_gatewayFailure_setsErrorStatus() {
        EligibilityInquiryDto request = buildRequest("CTL-ERR", "MEM-456");
        when(repository.existsByControlNumber("CTL-ERR")).thenReturn(false);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(payerGatewayClient.submitInquiry(any()))
                .thenThrow(new RuntimeException("Gateway timeout"));

        assertThatThrownBy(() -> eligibilityService.submitInquiry(request))
                .isInstanceOf(HealthConnectException.class)
                .hasMessageContaining("Payer gateway unavailable");

        // Capture the second save call to verify ERROR status was persisted
        ArgumentCaptor<EligibilityRequest> captor = ArgumentCaptor.forClass(EligibilityRequest.class);
        verify(repository, times(2)).save(captor.capture());
        EligibilityRequest savedWithError = captor.getAllValues().get(1);
        assertThat(savedWithError.getStatus()).isEqualTo(EligibilityStatus.ERROR);
        assertThat(savedWithError.getErrorMessage()).contains("Gateway timeout");
    }

    // ── getByControlNumber ────────────────────────────────────────────────────

    @Test
    @DisplayName("getByControlNumber: returns partial response when still PENDING")
    void getByControlNumber_pendingStatus_returnsPartial() {
        EligibilityRequest pending = EligibilityRequest.builder()
                .controlNumber("CTL-PEND")
                .status(EligibilityStatus.PENDING)
                .build();

        when(repository.findByControlNumber("CTL-PEND")).thenReturn(Optional.of(pending));

        EligibilityResponseDto result = eligibilityService.getByControlNumber("CTL-PEND");

        assertThat(result.getStatus()).isEqualTo("PENDING");
        assertThat(result.getBenefitsInformation()).isNull();
    }

    @Test
    @DisplayName("getByControlNumber: throws NOT_FOUND for unknown controlNumber")
    void getByControlNumber_notFound_throws() {
        when(repository.findByControlNumber("UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eligibilityService.getByControlNumber("UNKNOWN"))
                .isInstanceOf(HealthConnectException.class)
                .hasMessageContaining("No eligibility record");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private EligibilityInquiryDto buildRequest(String controlNumber, String memberId) {
        SubscriberDto subscriber = new SubscriberDto();
        subscriber.setMemberId(memberId);
        subscriber.setFirstName("John");
        subscriber.setLastName("Doe");
        subscriber.setDateOfBirth("19850315");

        EligibilityInquiryDto req = new EligibilityInquiryDto();
        req.setControlNumber(controlNumber);
        req.setTradingPartnerServiceId("BCBS-IL");
        req.setSubscriber(subscriber);
        return req;
    }

    private EligibilityResponseDto buildActiveResponse(String controlNumber) {
        PlanStatusDto planStatus = new PlanStatusDto();
        planStatus.setStatusCode("1");
        planStatus.setStatus("Active Coverage");

        EligibilityResponseDto response = new EligibilityResponseDto();
        response.setControlNumber(controlNumber);
        response.setStatus("SUCCESS");
        response.setPlanStatus(Collections.singletonList(planStatus));
        return response;
    }
}
