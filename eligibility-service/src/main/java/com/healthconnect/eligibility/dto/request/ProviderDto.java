package com.healthconnect.eligibility.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProviderDto {

    private String organizationName;
    private String firstName;
    private String lastName;
    private String npi;
    private String serviceProviderNumber;
    private String payorId;
    private String taxId;
    private String providerCode;
    private String providerType;
}
