package com.healthconnect.eligibility.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorDto {
    private String field;
    private String code;
    private String description;
    private String followupAction;
    private String location;
}
