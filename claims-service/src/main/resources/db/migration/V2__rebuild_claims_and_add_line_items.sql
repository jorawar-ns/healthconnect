-- ============================================================
-- V2 : Rebuild claims table + create claim_line_items
-- ============================================================
-- Context: V1 was scaffolded in Sprint 2 with wrong id type
-- (VARCHAR/UUID) and missing columns finalised during Sprint 3
-- design review (HCEP-1901).  Safe to rebuild in dev — no
-- production data has been migrated at this schema version.
-- ============================================================

-- 1. Drop old stub table (cascades to any FK children)
DROP TABLE IF EXISTS claim_line_items;
DROP TABLE IF EXISTS claims;

-- 2. Claims header table
CREATE TABLE claims (
    id                        BIGSERIAL       PRIMARY KEY,
    claim_number              VARCHAR(50)     NOT NULL UNIQUE,
    control_number            VARCHAR(50),
    payer_id                  VARCHAR(50)     NOT NULL,
    member_id                 VARCHAR(50)     NOT NULL,
    provider_npi              VARCHAR(20),
    provider_tin              VARCHAR(20),
    rendering_provider_npi    VARCHAR(20),
    claim_type                VARCHAR(30)     NOT NULL,           -- PROFESSIONAL | INSTITUTIONAL | DENTAL
    service_from_date         VARCHAR(10),                       -- CCYYMMDD (X12 DTP)
    service_to_date           VARCHAR(10),
    total_charge              NUMERIC(12, 2),
    allowed_amount            NUMERIC(12, 2),
    paid_amount               NUMERIC(12, 2),
    patient_responsibility    NUMERIC(12, 2),
    status                    VARCHAR(30)     NOT NULL DEFAULT 'RECEIVED',
    payer_claim_number        VARCHAR(100),                      -- populated post-adjudication
    icn                       VARCHAR(100),                      -- Insurance Control Number
    denial_reason_code        VARCHAR(20),
    denial_reason_description TEXT,
    raw_payload               TEXT,                              -- original 837 / JSON; JSONB pending DBA (HCEP-1901)
    submitted_at              TIMESTAMPTZ,
    adjudicated_at            TIMESTAMPTZ,
    created_at                TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at                TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_claims_claim_number    ON claims (claim_number);
CREATE INDEX idx_claims_control_number  ON claims (control_number);
CREATE INDEX idx_claims_payer_id        ON claims (payer_id);
CREATE INDEX idx_claims_member_id       ON claims (member_id);
CREATE INDEX idx_claims_status          ON claims (status);
CREATE INDEX idx_claims_submitted_at    ON claims (submitted_at DESC);

-- 3. Claim line-items (loop 2400 in X12 837)
CREATE TABLE claim_line_items (
    id                  BIGSERIAL       PRIMARY KEY,
    claim_id            BIGINT          NOT NULL REFERENCES claims (id) ON DELETE CASCADE,
    line_number         INTEGER         NOT NULL,
    procedure_code      VARCHAR(20)     NOT NULL,  -- CPT / HCPCS / Revenue code
    procedure_qualifier VARCHAR(10),               -- HC = CPT/HCPCS, ER = Revenue
    modifier_1          VARCHAR(10),
    modifier_2          VARCHAR(10),
    diagnosis_pointer   VARCHAR(20),               -- ICD-10 pointer
    service_date        VARCHAR(10),               -- CCYYMMDD
    units               NUMERIC(8, 2),
    charge_amount       NUMERIC(12, 2),
    allowed_amount      NUMERIC(12, 2),
    paid_amount         NUMERIC(12, 2),
    status              VARCHAR(30),
    denial_reason_code  VARCHAR(20),
    CONSTRAINT uq_claim_line_number UNIQUE (claim_id, line_number)
);

CREATE INDEX idx_claim_line_items_claim_id ON claim_line_items (claim_id);
