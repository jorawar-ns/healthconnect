package com.healthconnect.eligibility.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayerDto {
    private String entityIdentifier;
    private String entityType;
    private String name;
    private String payorIdentification;
}
