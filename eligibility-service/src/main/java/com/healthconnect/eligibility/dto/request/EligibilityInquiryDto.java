package com.healthconnect.eligibility.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Inbound request body — mirrors the MedicalEligibility schema from EligibilityV3.
 *
 * controlNumber and subscriber are the only required fields per the spec.
 * All others are optional and depend on the payer's requirements.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EligibilityInquiryDto {

    @NotBlank(message = "controlNumber is required")
    private String controlNumber;

    private String tradingPartnerServiceId;
    private String tradingPartnerName;
    private String submitterTransactionIdentifier;

    @Valid
    private ProviderDto provider;

    @NotNull(message = "subscriber is required")
    @Valid
    private SubscriberDto subscriber;

    private List<@Valid DependentDto> dependents;

    // Encounter holds service type codes and date range
    private EncounterDto encounter;
}
