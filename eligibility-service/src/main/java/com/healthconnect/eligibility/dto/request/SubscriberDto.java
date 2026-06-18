package com.healthconnect.eligibility.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Maps to the RequestSubscriber schema in EligibilityV3 OpenAPI spec.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubscriberDto {

    private String memberId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String suffix;
    private String gender;
    private String dateOfBirth;   // format: yyyyMMdd per X12 convention
    private String ssn;
    private String groupNumber;
    private String idCard;
    private String coverageLevelCode;
    private String providerCode;
    private String referenceIdentificationQualifier;
    private String providerIdentifier;
}
