package com.healthconnect.payer.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
public class PayerSearchCriteria{
    private String payerPlanName;
    private String status;
    private Boolean chiPayer;
    private Boolean assurancePayer;
    private Boolean lchcPayer;
    private Boolean enrollmentRequired;
    private LocalDate activationDateFrom;
    private LocalDate activationDateTo;
    private Instant lastUpdatedAfter;
}
