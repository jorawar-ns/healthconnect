package com.healthconnect.eligibility.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * Outbound response — mirrors the Response schema from EligibilityV3.
 * We surface the fields our downstream consumers actually use.
 * The raw X12 271 is stored in DB but not returned here (separate endpoint).
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EligibilityResponseDto {

    private String controlNumber;
    private String tradingPartnerServiceId;
    private String status;

    private ResponseMemberDto subscriber;
    private List<ResponseMemberDto> dependents;

    private PayerDto payer;
    private List<PlanStatusDto> planStatus;
    private List<BenefitsInfoDto> benefitsInformation;

    private List<ErrorDto> errors;
}
