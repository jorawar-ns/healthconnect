package com.healthconnect.payer.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "payers")
public class Payer {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(updatable = false, nullable = false)
    private String id;

    @Column(name = "payer_id", length = 36)
    private String payerId;

    @OneToMany(mappedBy = "payer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PayerState> states;

    @OneToMany(mappedBy = "payer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PayerAdditionalInfo> additionalInfo;

    @ElementCollection
    @CollectionTable(
            name = "payer_additional_ids",
            joinColumns = @JoinColumn(name = "payer_id")
    )
    @Column(name = "additional_id", length = 50)
    private Set<String> additionalIds = new HashSet<>();

    @Column(name = "industry_payer_id", length = 50)
    private String industryPayerId;

    @Column(name = "payer_plan_name", length = 50)
    private String payerPlanName;

    @Column(name = "payer_plan_alias")
    private String payerPlanAlias;

    @Column(name = "system", nullable = false, length = 50)
    private String system;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "connection_type", length = 50)
    private String connectionType;

    @Column(name = "report_type", length = 50)
    private String reportType;

    @Column(name = "attachment_type", length = 50)
    private String attachmentType;

    @Column(name = "transaction_type", length = 100)
    private String transactionType;

    @Column(name = "secondary_claims")
    private boolean secondaryClaims;

    @Column(name = "payer_stand_in")
    private boolean payerStandIn;

    @Column(name = "chi_payer")
    private boolean chiPayer;

    @Column(name = "assurance_payer")
    private boolean assurancePayer;

    @Column(name = "lchc_payer")
    private boolean lchcPayer;

    @Column(name = "allows_dual_clearinghouse_enroll")
    private boolean allowsDualClearinghouseEnroll;

    @Column(name = "par_payer")
    private boolean parPayer;

    @Column(name = "workers_compensation")
    private boolean workersCompensation;

    @Column(name = "enrollment_required")
    private boolean enrollmentRequired;

    @Column(name = "enrollment_notes")
    private String enrollmentNotes;

    @Column(name = "payer_notes")
    private String payerNotes;

    @Column(name = "setup_additional_payer_id", length = 50)
    private String setupAdditionalPayerId;

    @Column(name = "rpa_payer_id", length = 50)
    private String rpaPayerId;

    @Column(name = "imn_payer_id", length = 50)
    private String imnPayerId;

    @Column(name = "dental_payer_id", length = 50)
    private String dentalPayerId;

    @Column(name = "exchange_institutional_cpid", length = 50)
    private String exchangeInstitutionalCpid;

    @Column(name = "exchange_professional_cpid", length = 50)
    private String exchangeProfessionalCpid;

    @Column(name = "iedi_dental_cpid", length = 50)
    private String iediDentalCpid;

    @Column(name = "iedi_institutional_cpid", length = 50)
    private String iediInstitutionalCpid;

    @Column(name = "iedi_professional_cpid", length = 50)
    private String iediProfessionalCpid;

    @Column(name = "iedi_inst_claim_payer_id", length = 50)
    private String iediInstClaimPayerId;

    @Column(name = "iedi_prof_claim_payer_id", length = 50)
    private String iediProfClaimPayerId;

    @Column(name = "activation_date")
    private LocalDate activationDate;

    @Column(name = "deactivation_date")
    private LocalDate deactivationDate;

    @Column(name = "stand_in_indicator", length = 10)
    private String standInIndicator;

    @Column(name = "service_restored", length = 10)
    private String serviceRestored;

    @Column(name = "last_updated_after")
    private Instant lastUpdatedAfter;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

}
