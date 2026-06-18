-- V1 : ERA (Electronic Remittance Advice) Service schema
CREATE TABLE IF NOT EXISTS era_transactions (
    id                  VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::text,
    check_number        VARCHAR(50),
    payer_id            VARCHAR(50)  NOT NULL,
    payee_npi           VARCHAR(20),
    payment_amount      NUMERIC(12,2),
    payment_date        DATE,
    status              VARCHAR(30)  NOT NULL DEFAULT 'RECEIVED',
    raw_835             TEXT,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_era_payer_id      ON era_transactions(payer_id);
CREATE INDEX IF NOT EXISTS idx_era_payment_date  ON era_transactions(payment_date);
