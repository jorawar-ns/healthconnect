package com.healthconnect.eligibility.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthconnect.common.exception.HealthConnectException;
import com.healthconnect.eligibility.dto.request.EligibilityInquiryDto;
import com.healthconnect.eligibility.dto.request.SubscriberDto;
import com.healthconnect.eligibility.dto.response.EligibilityResponseDto;
import com.healthconnect.eligibility.service.EligibilityService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EligibilityController.class)
class EligibilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EligibilityService eligibilityService;

    @Test
    @DisplayName("GET /healthcheck returns 200")
    void healthCheck_returns200() throws Exception {
        mockMvc.perform(get("/medicalnetwork/eligibility/v3/healthcheck"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("POST / with valid body returns 200 and response")
    void submitInquiry_validRequest_returns200() throws Exception {
        EligibilityResponseDto mockResponse = new EligibilityResponseDto();
        mockResponse.setControlNumber("CTL-001");
        mockResponse.setStatus("SUCCESS");

        when(eligibilityService.submitInquiry(any(EligibilityInquiryDto.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/medicalnetwork/eligibility/v3/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildValidRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.controlNumber").value("CTL-001"));
    }

    @Test
    @DisplayName("POST / without controlNumber returns 422")
    void submitInquiry_missingControlNumber_returns422() throws Exception {
        EligibilityInquiryDto badRequest = new EligibilityInquiryDto();
        // controlNumber intentionally missing — should fail @NotBlank

        mockMvc.perform(post("/medicalnetwork/eligibility/v3/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST / without subscriber returns 422")
    void submitInquiry_missingSubscriber_returns422() throws Exception {
        EligibilityInquiryDto badRequest = new EligibilityInquiryDto();
        badRequest.setControlNumber("CTL-001");
        // subscriber missing — should fail @NotNull

        mockMvc.perform(post("/medicalnetwork/eligibility/v3/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badRequest)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("GET /{controlNumber} returns 404 when not found")
    void getByControlNumber_notFound_returns404() throws Exception {
        when(eligibilityService.getByControlNumber("MISSING"))
                .thenThrow(new HealthConnectException(
                        "No eligibility record for controlNumber: MISSING",
                        HttpStatus.NOT_FOUND, "ELIGIBILITY_NOT_FOUND"));

        mockMvc.perform(get("/medicalnetwork/eligibility/v3/MISSING"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("ELIGIBILITY_NOT_FOUND"));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private EligibilityInquiryDto buildValidRequest() {
        SubscriberDto subscriber = new SubscriberDto();
        subscriber.setMemberId("MEM-123");
        subscriber.setFirstName("Jane");
        subscriber.setLastName("Smith");

        EligibilityInquiryDto req = new EligibilityInquiryDto();
        req.setControlNumber("CTL-001");
        req.setTradingPartnerServiceId("BCBS-IL");
        req.setSubscriber(subscriber);
        return req;
    }
}
