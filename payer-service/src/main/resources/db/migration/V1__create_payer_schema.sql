-- V1 : com.healthconnect.payer.entity.Payer Service schema
-- NOTE: Full schema implemented in Sprint 3 by Jorawar Singh
-- This migration creates the table structure matching payerListV1 OpenAPI spec

CREATE TABLE IF NOT EXISTS payers (
    id                              VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::text,
    payer_id                        VARCHAR(50),
    industry_payer_id               VARCHAR(50),
    payer_plan_name                 VARCHAR(255),
    payer_plan_alias                VARCHAR(255),
    system                          VARCHAR(50)  NOT NULL,
    status                          VARCHAR(20)  NOT NULL DEFAULT 'Active',
    connection_type                 VARCHAR(50),
    report_type                     VARCHAR(50),
    attachment_type                 VARCHAR(50),
    transaction_type                VARCHAR(100),
    secondary_claims                BOOLEAN      DEFAULT FALSE,
    payer_stand_in                  BOOLEAN      DEFAULT FALSE,
    chi_payer                       BOOLEAN      DEFAULT FALSE,
    assurance_payer                 BOOLEAN      DEFAULT FALSE,
    lchc_payer                      BOOLEAN      DEFAULT FALSE,
    allows_dual_clearinghouse_enroll BOOLEAN     DEFAULT FALSE,
    par_payer                       BOOLEAN      DEFAULT FALSE,
    workers_compensation            BOOLEAN      DEFAULT FALSE,
    enrollment_required             BOOLEAN      DEFAULT FALSE,
    enrollment_notes                TEXT,
    payer_notes                     TEXT,
    setup_additional_payer_id       VARCHAR(50),
    rpa_payer_id                    VARCHAR(50),
    imn_payer_id                    VARCHAR(50),
    dental_payer_id                 VARCHAR(50),
    -- linked payer IDs
    exchange_institutional_cpid     VARCHAR(50),
    exchange_professional_cpid      VARCHAR(50),
    iedi_dental_cpid                VARCHAR(50),
    iedi_institutional_cpid         VARCHAR(50),
    iedi_professional_cpid          VARCHAR(50),
    iedi_inst_claim_payer_id        VARCHAR(50),
    iedi_prof_claim_payer_id        VARCHAR(50),
    -- dates
    activation_date                 DATE,
    deactivation_date               DATE,
    stand_in_indicator              VARCHAR(10),
    service_restored                VARCHAR(10),
    last_updated_after              TIMESTAMPTZ,
    created_at                      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at                      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS payer_states (
    payer_id   VARCHAR(36)  NOT NULL REFERENCES payers(id) ON DELETE CASCADE,
    label      VARCHAR(100),
    value      VARCHAR(10)  NOT NULL,
    PRIMARY KEY (payer_id, value)
);

CREATE TABLE IF NOT EXISTS payer_additional_info (
    id        BIGSERIAL    PRIMARY KEY,
    payer_id  VARCHAR(36)  NOT NULL REFERENCES payers(id) ON DELETE CASCADE,
    key       VARCHAR(100),
    value     TEXT
);

CREATE TABLE IF NOT EXISTS payer_additional_ids (
    payer_id        VARCHAR(36)  NOT NULL REFERENCES payers(id) ON DELETE CASCADE,
    additional_id   VARCHAR(50)  NOT NULL,
    PRIMARY KEY (payer_id, additional_id)
);

-- Indexes for the most common filter params
CREATE INDEX IF NOT EXISTS idx_payer_payer_id           ON payers(payer_id);
CREATE INDEX IF NOT EXISTS idx_payer_industry_payer_id  ON payers(industry_payer_id);
CREATE INDEX IF NOT EXISTS idx_payer_plan_name          ON payers(payer_plan_name);
CREATE INDEX IF NOT EXISTS idx_payer_system             ON payers(system);
CREATE INDEX IF NOT EXISTS idx_payer_status             ON payers(status);
CREATE INDEX IF NOT EXISTS idx_payer_activation_date    ON payers(activation_date);
CREATE INDEX IF NOT EXISTS idx_payer_deactivation_date  ON payers(deactivation_date);
CREATE INDEX IF NOT EXISTS idx_payer_updated_at         ON payers(updated_at);
