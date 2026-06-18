package com.healthconnect.claims.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClaimResponseDto {

    private Long id;
    private String claimNumber;
    private String payerId;
    private String memberId;
    private String providerNpi;
    private String providerTin;
    private String renderingProviderNpi;
    private String claimType;
    private String serviceFromDate;
    private String serviceToDate;
    private BigDecimal totalCharge;
    private BigDecimal allowedAmount;
    private BigDecimal paidAmount;
    private BigDecimal patientResponsibility;
    private String status;
    private String payerClaimNumber;
    private String controlNumber;
    private String icn;
    private String denialReasonCode;
    private String denialReasonDescription;
    private Instant submittedAt;
    private Instant adjudicatedAt;
    private Instant createdAt;
    private List<ClaimLineItemResponseDto> lineItems;
}
