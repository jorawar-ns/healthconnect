package com.healthconnect.claims.dto.request;

import com.healthconnect.claims.entity.ClaimType;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Request body for POST /claims/submit.
 *
 * Models the minimum required fields from an 837P/I/D transaction.
 * Full X12 parsing is handled by the EDI gateway (future — HCEP-2044).
 */
@Data
public class SubmitClaimDto {

    @NotBlank(message = "controlNumber is required")
    private String controlNumber;

    @NotBlank(message = "payerId is required")
    private String payerId;

    @NotBlank(message = "memberId is required")
    private String memberId;

    @NotBlank(message = "providerNpi is required")
    private String providerNpi;

    private String providerTin;
    private String renderingProviderNpi;

    @NotNull(message = "claimType is required")
    private ClaimType claimType;

    private String serviceFromDate;
    private String serviceToDate;

    @NotEmpty(message = "At least one line item is required")
    @Valid
    private List<ClaimLineItemDto> lineItems;
}
