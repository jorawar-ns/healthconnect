-- ============================================================
-- V1 : Eligibility Service schema
-- ============================================================

CREATE TABLE IF NOT EXISTS eligibility_requests (
    id                          BIGSERIAL       PRIMARY KEY,
    control_number              VARCHAR(50)     NOT NULL UNIQUE,
    trading_partner_service_id  VARCHAR(50),
    trading_partner_name        VARCHAR(100),
    member_id                   VARCHAR(50),
    member_first_name           VARCHAR(80),
    member_last_name            VARCHAR(80),
    member_dob                  DATE,
    provider_npi                VARCHAR(20),
    provider_org_name           VARCHAR(150),
    service_date                DATE,
    status                      VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    request_payload             TEXT,
    response_payload            TEXT,
    error_message               VARCHAR(500),
    created_at                  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at                  TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_elig_control_number  ON eligibility_requests(control_number);
CREATE INDEX IF NOT EXISTS idx_elig_member_id       ON eligibility_requests(member_id);
CREATE INDEX IF NOT EXISTS idx_elig_status          ON eligibility_requests(status);
CREATE INDEX IF NOT EXISTS idx_elig_created_at      ON eligibility_requests(created_at);
