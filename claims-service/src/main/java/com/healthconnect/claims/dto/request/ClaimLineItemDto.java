package com.healthconnect.claims.dto.request;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class ClaimLineItemDto {

    @NotNull(message = "lineNumber is required")
    @Min(value = 1, message = "lineNumber must be >= 1")
    private Integer lineNumber;

    @NotBlank(message = "procedureCode is required")
    private String procedureCode;

    /** HC = CPT/HCPCS, ER = Revenue Code */
    private String procedureQualifier;

    private String modifier1;
    private String modifier2;
    private String diagnosisPointer;
    private String serviceDate;

    @NotNull(message = "units is required")
    @DecimalMin(value = "0.01", message = "units must be positive")
    private BigDecimal units;

    @NotNull(message = "chargeAmount is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "chargeAmount must be non-negative")
    private BigDecimal chargeAmount;
}
