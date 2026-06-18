-- V1 : Claims Service schema
CREATE TABLE IF NOT EXISTS claims (
    id               VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::text,
    claim_number     VARCHAR(50)  NOT NULL UNIQUE,
    payer_id         VARCHAR(50)  NOT NULL,
    member_id        VARCHAR(50)  NOT NULL,
    provider_npi     VARCHAR(20),
    claim_type       VARCHAR(30)  NOT NULL,
    total_charge     NUMERIC(12,2),
    status           VARCHAR(30)  NOT NULL DEFAULT 'RECEIVED',
    submitted_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    adjudicated_at   TIMESTAMPTZ,
    payload          JSONB,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_claims_payer_id   ON claims(payer_id);
CREATE INDEX IF NOT EXISTS idx_claims_member_id  ON claims(member_id);
CREATE INDEX IF NOT EXISTS idx_claims_status     ON claims(status);
