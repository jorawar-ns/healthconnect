package com.healthconnect.eligibility.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Persists each 270/271 eligibility transaction for audit and re-query purposes.
 *
 * The raw request and response payloads are stored as JSON text columns.
 * JSONB was proposed but the DBA team hasn't approved the column type
 * for this service yet — using TEXT for now (tracked in HCEP-1142).
 */
@Entity
@Table(name = "eligibility_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EligibilityRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "control_number", nullable = false, unique = true, length = 50)
    private String controlNumber;

    @Column(name = "trading_partner_service_id", length = 50)
    private String tradingPartnerServiceId;

    @Column(name = "trading_partner_name", length = 100)
    private String tradingPartnerName;

    // Subscriber / member identifiers
    @Column(name = "member_id", length = 50)
    private String memberId;

    @Column(name = "member_first_name", length = 80)
    private String memberFirstName;

    @Column(name = "member_last_name", length = 80)
    private String memberLastName;

    @Column(name = "member_dob")
    private LocalDate memberDob;

    // Provider
    @Column(name = "provider_npi", length = 20)
    private String providerNpi;

    @Column(name = "provider_org_name", length = 150)
    private String providerOrgName;

    // Service date requested
    @Column(name = "service_date")
    private LocalDate serviceDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EligibilityStatus status = EligibilityStatus.PENDING;

    // Full JSON payloads — stored as TEXT, parsed on demand
    @Column(name = "request_payload", columnDefinition = "TEXT")
    private String requestPayload;

    @Column(name = "response_payload", columnDefinition = "TEXT")
    private String responsePayload;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
