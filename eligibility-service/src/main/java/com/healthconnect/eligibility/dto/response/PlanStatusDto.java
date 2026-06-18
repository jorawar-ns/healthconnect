package com.healthconnect.eligibility.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanStatusDto {

    private String statusCode;
    private String status;
    private String planDetails;
    private List<String> serviceTypeCodes;
}
