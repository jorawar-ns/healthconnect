package com.healthconnect.eligibility.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseMemberDto {
    private String memberId;
    private String firstName;
    private String lastName;
    private String middleName;
    private String gender;
    private String dateOfBirth;
    private String groupNumber;
    private String planNumber;
    private String relationToSubscriber;
    private String entityIdentifier;
}
