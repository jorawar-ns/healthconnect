package com.healthconnect.claims.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Individual service line on a claim (loop 2400 in X12 837).
 *
 * Each line corresponds to one procedure performed during the service encounter.
 */
@Entity
@Table(name = "claim_line_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimLineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", nullable = false)
    private Claim claim;

    /** Line number on the claim (1-based). */
    @Column(name = "line_number", nullable = false)
    private Integer lineNumber;

    /** CPT / HCPCS / Revenue code. */
    @Column(name = "procedure_code", nullable = false)
    private String procedureCode;

    /** Qualifier: HC = CPT/HCPCS, ER = Revenue. */
    @Column(name = "procedure_qualifier")
    private String procedureQualifier;

    @Column(name = "modifier_1")
    private String modifier1;

    @Column(name = "modifier_2")
    private String modifier2;

    /** ICD-10 diagnosis pointer. */
    @Column(name = "diagnosis_pointer")
    private String diagnosisPointer;

    @Column(name = "service_date")
    private String serviceDate;

    @Column(name = "units", precision = 8, scale = 2)
    private BigDecimal units;

    @Column(name = "charge_amount", precision = 12, scale = 2)
    private BigDecimal chargeAmount;

    @Column(name = "allowed_amount", precision = 12, scale = 2)
    private BigDecimal allowedAmount;

    @Column(name = "paid_amount", precision = 12, scale = 2)
    private BigDecimal paidAmount;

    @Enumerated(javax.persistence.EnumType.STRING)
    @Column(name = "status")
    private ClaimStatus status;

    @Column(name = "denial_reason_code")
    private String denialReasonCode;
}
