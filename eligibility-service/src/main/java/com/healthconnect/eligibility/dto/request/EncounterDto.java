package com.healthconnect.eligibility.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EncounterDto {

    private String beginningDateOfService;   // yyyyMMdd
    private String endDateOfService;
    private List<String> serviceTypeCodes;
}
