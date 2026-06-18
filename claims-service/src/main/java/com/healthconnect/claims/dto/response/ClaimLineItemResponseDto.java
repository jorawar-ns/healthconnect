package com.healthconnect.claims.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ClaimLineItemResponseDto {

    private Long id;
    private Integer lineNumber;
    private String procedureCode;
    private String procedureQualifier;
    private String modifier1;
    private String modifier2;
    private String diagnosisPointer;
    private String serviceDate;
    private BigDecimal units;
    private BigDecimal chargeAmount;
    private BigDecimal allowedAmount;
    private BigDecimal paidAmount;
    private String status;
    private String denialReasonCode;
}
