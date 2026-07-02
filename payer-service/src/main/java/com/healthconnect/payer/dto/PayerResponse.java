package com.healthconnect.payer.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PayerResponse {
    private String id;
    private String payerPlanName;
    private String status;
    private Boolean chiPayer;
    private Boolean assurancePayer;
    private Boolean enrollmentRequired;
    private LocalDate activationDate;
}
