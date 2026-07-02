-- V2 : Seed data for local development and testing
-- 15 realistic payers covering a mix of:
--   status (Active / Inactive), system (PROF / INST / DENTAL),
--   chi/assurance/lchc/enrollment flags, and date ranges

INSERT INTO payers (
    id, payer_id, industry_payer_id, payer_plan_name, payer_plan_alias,
    system, status, connection_type, report_type,
    secondary_claims, chi_payer, assurance_payer, lchc_payer,
    enrollment_required, workers_compensation,
    activation_date, deactivation_date, last_updated_after,
    created_at, updated_at
) VALUES

-- 1. UnitedHealthcare Professional
('a1b2c3d4-0001-0001-0001-000000000001',
 'UHC001', 'UHC-PROF-001', 'UnitedHealthcare Professional', 'UHC Prof',
 'PROF', 'Active', 'EDI', 'Standard',
 true, true, false, false,
 true, false,
 '2018-01-01', NULL, '2024-01-15T10:00:00Z',
 NOW(), NOW()),

-- 2. UnitedHealthcare Institutional
('a1b2c3d4-0002-0002-0002-000000000002',
 'UHC002', 'UHC-INST-001', 'UnitedHealthcare Institutional', 'UHC Inst',
 'INST', 'Active', 'EDI', 'Standard',
 true, true, false, false,
 true, false,
 '2018-01-01', NULL, '2024-01-15T10:00:00Z',
 NOW(), NOW()),

-- 3. Blue Cross Blue Shield Illinois
('a1b2c3d4-0003-0003-0003-000000000003',
 'BCBSIL', 'BCBS-IL-001', 'Blue Cross Blue Shield Illinois', 'BCBS IL',
 'PROF', 'Active', 'EDI', 'Standard',
 true, false, true, false,
 true, false,
 '2019-03-15', NULL, '2024-02-01T08:00:00Z',
 NOW(), NOW()),

-- 4. Blue Cross Blue Shield Texas
('a1b2c3d4-0004-0004-0004-000000000004',
 'BCBSTX', 'BCBS-TX-001', 'Blue Cross Blue Shield Texas', 'BCBS TX',
 'PROF', 'Active', 'EDI', 'Standard',
 true, false, true, false,
 false, false,
 '2019-06-01', NULL, '2024-02-01T08:00:00Z',
 NOW(), NOW()),

-- 5. Aetna Professional
('a1b2c3d4-0005-0005-0005-000000000005',
 'AET001', 'AET-PROF-001', 'Aetna Professional', 'Aetna Prof',
 'PROF', 'Active', 'EDI', 'Standard',
 false, false, false, true,
 true, false,
 '2020-01-01', NULL, '2024-03-10T14:00:00Z',
 NOW(), NOW()),

-- 6. Aetna Institutional
('a1b2c3d4-0006-0006-0006-000000000006',
 'AET002', 'AET-INST-001', 'Aetna Institutional', 'Aetna Inst',
 'INST', 'Active', 'EDI', 'Standard',
 false, false, false, true,
 true, false,
 '2020-01-01', NULL, '2024-03-10T14:00:00Z',
 NOW(), NOW()),

-- 7. Cigna Healthcare Professional
('a1b2c3d4-0007-0007-0007-000000000007',
 'CIG001', 'CIG-PROF-001', 'Cigna Healthcare Professional', 'Cigna Prof',
 'PROF', 'Active', 'EDI', 'Standard',
 true, false, false, false,
 true, false,
 '2017-09-01', NULL, '2023-11-20T09:00:00Z',
 NOW(), NOW()),

-- 8. Humana Professional
('a1b2c3d4-0008-0008-0008-000000000008',
 'HUM001', 'HUM-PROF-001', 'Humana Professional', 'Humana Prof',
 'PROF', 'Active', 'EDI', 'ERA',
 true, false, false, false,
 false, false,
 '2021-04-01', NULL, '2024-04-05T11:00:00Z',
 NOW(), NOW()),

-- 9. Medicare Part B (CHI Payer)
('a1b2c3d4-0009-0009-0009-000000000009',
 'MCR001', 'MEDICARE-B', 'Medicare Part B', 'Medicare B',
 'PROF', 'Active', 'EDI', 'Standard',
 false, true, false, false,
 true, false,
 '2015-01-01', NULL, '2024-01-01T00:00:00Z',
 NOW(), NOW()),

-- 10. Medicaid Illinois (CHI + LCHC Payer)
('a1b2c3d4-0010-0010-0010-000000000010',
 'MCD-IL', 'MEDICAID-IL', 'Medicaid Illinois', 'IL Medicaid',
 'PROF', 'Active', 'EDI', 'Standard',
 false, true, false, true,
 true, false,
 '2015-01-01', NULL, '2024-01-01T00:00:00Z',
 NOW(), NOW()),

-- 11. Workers Compensation Fund
('a1b2c3d4-0011-0011-0011-000000000011',
 'WCF001', 'WC-FUND-001', 'Workers Compensation Fund', 'WC Fund',
 'PROF', 'Active', 'EDI', 'Standard',
 false, false, false, false,
 true, true,
 '2019-01-01', NULL, '2023-06-01T10:00:00Z',
 NOW(), NOW()),

-- 12. Delta Dental Professional
('a1b2c3d4-0012-0012-0012-000000000012',
 'DDT001', 'DELTA-DEN-001', 'Delta Dental Professional', 'Delta Dental',
 'DENTAL', 'Active', 'EDI', 'Standard',
 false, false, false, false,
 true, false,
 '2020-07-01', NULL, '2023-09-15T08:00:00Z',
 NOW(), NOW()),

-- 13. Molina Healthcare (Inactive — deactivated)
('a1b2c3d4-0013-0013-0013-000000000013',
 'MOL001', 'MOL-PROF-001', 'Molina Healthcare Professional', 'Molina',
 'PROF', 'Inactive', 'EDI', 'Standard',
 false, false, true, false,
 false, false,
 '2018-03-01', '2023-12-31', '2023-12-31T23:59:00Z',
 NOW(), NOW()),

-- 14. Coventry Health Care (Inactive)
('a1b2c3d4-0014-0014-0014-000000000014',
 'COV001', 'COV-PROF-001', 'Coventry Health Care Professional', 'Coventry',
 'PROF', 'Inactive', 'EDI', 'Standard',
 true, false, false, false,
 true, false,
 '2016-01-01', '2022-06-30', '2022-06-30T23:59:00Z',
 NOW(), NOW()),

-- 15. Oscar Health Professional
('a1b2c3d4-0015-0015-0015-000000000015',
 'OSC001', 'OSC-PROF-001', 'Oscar Health Professional', 'Oscar Health',
 'PROF', 'Active', 'EDI', 'Standard',
 false, false, false, false,
 true, false,
 '2022-01-01', NULL, '2024-05-01T12:00:00Z',
 NOW(), NOW());


-- Seed payer_states for a few payers
INSERT INTO payer_states (payer_id, label, value) VALUES
('a1b2c3d4-0001-0001-0001-000000000001', 'Illinois', 'IL'),
('a1b2c3d4-0001-0001-0001-000000000001', 'Wisconsin', 'WI'),
('a1b2c3d4-0001-0001-0001-000000000001', 'Indiana', 'IN'),
('a1b2c3d4-0003-0003-0003-000000000003', 'Illinois', 'IL'),
('a1b2c3d4-0009-0009-0009-000000000009', 'Illinois', 'IL'),
('a1b2c3d4-0009-0009-0009-000000000009', 'Wisconsin', 'WI'),
('a1b2c3d4-0010-0010-0010-000000000010', 'Illinois', 'IL');


-- Seed payer_additional_ids for UHC
INSERT INTO payer_additional_ids (payer_id, additional_id) VALUES
('a1b2c3d4-0001-0001-0001-000000000001', 'UHC-ALT-001'),
('a1b2c3d4-0001-0001-0001-000000000001', 'UHC-ALT-002'),
('a1b2c3d4-0002-0002-0002-000000000002', 'UHC-INST-ALT-001');
