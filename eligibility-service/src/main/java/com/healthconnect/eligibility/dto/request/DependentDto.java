package com.healthconnect.eligibility.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DependentDto {

    private String memberId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;
    private String dateOfBirth;
    private String ssn;
    private String groupNumber;
    private String individualRelationshipCode;
    private String eligibilityCategory;
}
