package com.healthconnect.eligibility.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BenefitsInfoDto {

    private String code;
    private String name;
    private String coverageLevelCode;
    private String coverageLevel;
    private List<String> serviceTypeCodes;
    private List<String> serviceTypes;
    private String insuranceTypeCode;
    private String insuranceType;
    private String planCoverage;
    private String benefitAmount;
    private String benefitPercent;
    private String timeQualifierCode;
    private String timeQualifier;
    private String authOrCertIndicator;
    private String inPlanNetworkIndicatorCode;
    private String inPlanNetworkIndicator;
}
