-- V1 : Authorization (Prior Auth) Service schema
CREATE TABLE IF NOT EXISTS prior_auths (
    id               VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::text,
    auth_number      VARCHAR(50)  NOT NULL UNIQUE,
    payer_id         VARCHAR(50)  NOT NULL,
    member_id        VARCHAR(50)  NOT NULL,
    procedure_code   VARCHAR(20),
    diagnosis_code   VARCHAR(20),
    status           VARCHAR(30)  NOT NULL DEFAULT 'PENDING',
    valid_from       DATE,
    valid_to         DATE,
    notes            TEXT,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_auth_payer_id   ON prior_auths(payer_id);
CREATE INDEX IF NOT EXISTS idx_auth_member_id  ON prior_auths(member_id);
