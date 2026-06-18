-- V1 : Document Service schema
CREATE TABLE IF NOT EXISTS documents (
    id            VARCHAR(36)   PRIMARY KEY DEFAULT gen_random_uuid()::text,
    file_name     VARCHAR(255)  NOT NULL,
    content_type  VARCHAR(100),
    s3_key        VARCHAR(512)  NOT NULL UNIQUE,
    s3_bucket     VARCHAR(100)  NOT NULL,
    reference_id  VARCHAR(50),      -- e.g. claim_id, auth_number
    reference_type VARCHAR(50),     -- CLAIM | AUTH | ELIGIBILITY
    uploaded_by   VARCHAR(36),
    file_size     BIGINT,
    created_at    TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_docs_reference ON documents(reference_id, reference_type);
