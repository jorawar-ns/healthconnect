-- V3 : Extended real payer seed data
-- Covers major commercial, government, regional, and behavioral health payers

INSERT INTO payers (
    id, payer_id, industry_payer_id, payer_plan_name, payer_plan_alias,
    system, status, connection_type, report_type,
    secondary_claims, chi_payer, assurance_payer, lchc_payer,
    enrollment_required, workers_compensation,
    activation_date, deactivation_date, last_updated_after,
    created_at, updated_at
) VALUES

-- ── ANTHEM / BCBS FAMILY ─────────────────────────────────────────────────────

('b2c3d4e5-0001-0001-0001-000000000101',
 'ANT-OH', 'ANT-BCBS-OH', 'Anthem Blue Cross Blue Shield Ohio', 'Anthem OH',
 'PROF', 'Active', 'EDI', 'Standard',
 true, false, true, false, true, false,
 '2017-01-01', NULL, '2024-03-01T08:00:00Z', NOW(), NOW()),

('b2c3d4e5-0002-0002-0002-000000000102',
 'ANT-IN', 'ANT-BCBS-IN', 'Anthem Blue Cross Blue Shield Indiana', 'Anthem IN',
 'PROF', 'Active', 'EDI', 'Standard',
 true, false, true, false, true, false,
 '2017-01-01', NULL, '2024-03-01T08:00:00Z', NOW(), NOW()),

('b2c3d4e5-0003-0003-0003-000000000103',
 'ANT-VA', 'ANT-BCBS-VA', 'Anthem Blue Cross Blue Shield Virginia', 'Anthem VA',
 'PROF', 'Active', 'EDI', 'Standard',
 true, false, true, false, true, false,
 '2017-06-01', NULL, '2024-03-01T08:00:00Z', NOW(), NOW()),

('b2c3d4e5-0004-0004-0004-000000000104',
 'ANT-GA', 'ANT-BCBS-GA', 'Anthem Blue Cross Blue Shield Georgia', 'Anthem GA',
 'PROF', 'Active', 'EDI', 'Standard',
 true, false, false, false, true, false,
 '2018-01-01', NULL, '2024-03-01T08:00:00Z', NOW(), NOW()),

('b2c3d4e5-0005-0005-0005-000000000105',
 'ANT-CT', 'ANT-BCBS-CT', 'Anthem Blue Cross Blue Shield Connecticut', 'Anthem CT',
 'PROF', 'Active', 'EDI', 'Standard',
 true, false, false, false, false, false,
 '2018-06-01', NULL, '2024-03-01T08:00:00Z', NOW(), NOW()),

('b2c3d4e5-0006-0006-0006-000000000106',
 'EMP-NY', 'EMPIRE-NY', 'Empire BlueCross BlueShield New York', 'Empire BCBS NY',
 'PROF', 'Active', 'EDI', 'Standard',
 true, false, false, false, true, false,
 '2016-01-01', NULL, '2024-01-10T09:00:00Z', NOW(), NOW()),

('b2c3d4e5-0007-0007-0007-000000000107',
 'EMP-NY-I', 'EMPIRE-NY-INST', 'Empire BlueCross BlueShield New York Institutional', 'Empire BCBS NY Inst',
 'INST', 'Active', 'EDI', 'Standard',
 true, false, false, false, true, false,
 '2016-01-01', NULL, '2024-01-10T09:00:00Z', NOW(), NOW()),

('b2c3d4e5-0008-0008-0008-000000000108',
 'EXC-NY', 'EXCELLUS-NY', 'Excellus BlueCross BlueShield', 'Excellus BCBS',
 'PROF', 'Active', 'EDI', 'Standard',
 true, false, false, false, true, false,
 '2019-01-01', NULL, '2023-12-01T08:00:00Z', NOW(), NOW()),

('b2c3d4e5-0009-0009-0009-000000000109',
 'HIG-PA', 'HIGHMARK-PA', 'Highmark Blue Cross Blue Shield Pennsylvania', 'Highmark PA',
 'PROF', 'Active', 'EDI', 'Standard',
 true, false, false, false, true, false,
 '2016-06-01', NULL, '2024-02-15T10:00:00Z', NOW(), NOW()),

('b2c3d4e5-0010-0010-0010-000000000110',
 'HIG-WV', 'HIGHMARK-WV', 'Highmark Blue Cross Blue Shield West Virginia', 'Highmark WV',
 'PROF', 'Active', 'EDI', 'Standard',
 false, false, false, false, true, false,
 '2017-01-01', NULL, '2024-02-15T10:00:00Z', NOW(), NOW()),

('b2c3d4e5-0011-0011-0011-000000000111',
 'CAR-MD', 'CAREFIRST-MD', 'CareFirst BlueCross BlueShield Maryland', 'CareFirst MD',
 'PROF', 'Active', 'EDI', 'Standard',
 true, false, false, false, true, false,
 '2018-01-01', NULL, '2024-01-20T09:00:00Z', NOW(), NOW()),

('b2c3d4e5-0012-0012-0012-000000000112',
 'IBC-PA', 'IBC-PA', 'Independence Blue Cross Pennsylvania', 'Independence BC',
 'PROF', 'Active', 'EDI', 'ERA',
 true, false, false, false, true, false,
 '2016-01-01', NULL, '2024-01-05T08:00:00Z', NOW(), NOW()),

('b2c3d4e5-0013-0013-0013-000000000113',
 'BCBS-NC', 'BCBS-NC-001', 'Blue Cross Blue Shield North Carolina', 'BCBS NC',
 'PROF', 'Active', 'EDI', 'Standard',
 true, false, false, false, true, false,
 '2017-03-01', NULL, '2024-02-01T08:00:00Z', NOW(), NOW()),

('b2c3d4e5-0014-0014-0014-000000000114',
 'BCBS-MA', 'BCBS-MA-001', 'Blue Cross Blue Shield Massachusetts', 'BCBS MA',
 'PROF', 'Active', 'EDI', 'ERA',
 true, false, false, false, true, false,
 '2015-01-01', NULL, '2024-03-10T08:00:00Z', NOW(), NOW()),

('b2c3d4e5-0015-0015-0015-000000000115',
 'BCBS-MN', 'BCBS-MN-001', 'Blue Cross Blue Shield Minnesota', 'BCBS MN',
 'PROF', 'Active', 'EDI', 'Standard',
 true, false, false, false, true, false,
 '2016-01-01', NULL, '2024-01-15T08:00:00Z', NOW(), NOW()),

-- ── GOVERNMENT / FEDERAL ─────────────────────────────────────────────────────

('b2c3d4e5-0016-0016-0016-000000000116',
 'MCR-A', 'MEDICARE-A', 'Medicare Part A', 'Medicare A',
 'INST', 'Active', 'EDI', 'Standard',
 false, true, false, false, true, false,
 '2010-01-01', NULL, '2024-01-01T00:00:00Z', NOW(), NOW()),

('b2c3d4e5-0017-0017-0017-000000000117',
 'MCR-ADV', 'MEDICARE-ADV', 'Medicare Advantage', 'Medicare Adv',
 'PROF', 'Active', 'EDI', 'ERA',
 false, true, false, false, true, false,
 '2012-01-01', NULL, '2024-01-01T00:00:00Z', NOW(), NOW()),

('b2c3d4e5-0018-0018-0018-000000000118',
 'MCD-MI', 'MEDICAID-MI', 'Medicaid Michigan', 'MI Medicaid',
 'PROF', 'Active', 'EDI', 'Standard',
 false, true, false, true, true, false,
 '2015-01-01', NULL, '2024-01-01T00:00:00Z', NOW(), NOW()),

('b2c3d4e5-0019-0019-0019-000000000119',
 'MCD-WI', 'MEDICAID-WI', 'Medicaid Wisconsin', 'WI Medicaid',
 'PROF', 'Active', 'EDI', 'Standard',
 false, true, false, true, true, false,
 '2015-01-01', NULL, '2024-01-01T00:00:00Z', NOW(), NOW()),

('b2c3d4e5-0020-0020-0020-000000000120',
 'MCD-IN', 'MEDICAID-IN', 'Medicaid Indiana', 'IN Medicaid',
 'PROF', 'Active', 'EDI', 'Standard',
 false, true, false, false, true, false,
 '2015-06-01', NULL, '2024-01-01T00:00:00Z', NOW(), NOW()),

('b2c3d4e5-0021-0021-0021-000000000121',
 'TRI-E', 'TRICARE-EAST', 'Tricare East', 'Tricare East',
 'PROF', 'Active', 'EDI', 'Standard',
 false, false, false, false, true, false,
 '2018-01-01', NULL, '2023-10-01T08:00:00Z', NOW(), NOW()),

('b2c3d4e5-0022-0022-0022-000000000122',
 'TRI-W', 'TRICARE-WEST', 'Tricare West', 'Tricare West',
 'PROF', 'Active', 'EDI', 'Standard',
 false, false, false, false, true, false,
 '2018-01-01', NULL, '2023-10-01T08:00:00Z', NOW(), NOW()),

('b2c3d4e5-0023-0023-0023-000000000123',
 'CVA', 'CHAMPVA', 'ChampVA', 'ChampVA',
 'PROF', 'Active', 'EDI', 'Standard',
 false, false, false, false, true, false,
 '2016-01-01', NULL, '2023-06-01T08:00:00Z', NOW(), NOW()),

-- ── CENTENE / REGIONAL MANAGED CARE ─────────────────────────────────────────

('b2c3d4e5-0024-0024-0024-000000000124',
 'AMB-IL', 'AMBETTER-IL', 'Ambetter of Illinois', 'Ambetter IL',
 'PROF', 'Active', 'EDI', 'Standard',
 false, false, false, false, true, false,
 '2020-01-01', NULL, '2024-04-01T09:00:00Z', NOW(), NOW()),

('b2c3d4e5-0025-0025-0025-000000000125',
 'WLC-FL', 'WELLCARE-FL', 'WellCare of Florida', 'WellCare FL',
 'PROF', 'Active', 'EDI', 'Standard',
 false, false, false, false, false, false,
 '2019-01-01', NULL, '2024-02-10T08:00:00Z', NOW(), NOW()),

('b2c3d4e5-0026-0026-0026-000000000126',
 'SSH-FL', 'SUNSHINE-FL', 'Sunshine Health Florida', 'Sunshine FL',
 'PROF', 'Active', 'EDI', 'Standard',
 false, false, false, false, false, false,
 '2020-06-01', NULL, '2024-03-01T08:00:00Z', NOW(), NOW()),

('b2c3d4e5-0027-0027-0027-000000000127',
 'STAY-FL', 'STAYWELL-FL', 'Staywell Florida', 'Staywell FL',
 'PROF', 'Inactive', 'EDI', 'Standard',
 false, false, false, false, false, false,
 '2017-01-01', '2023-01-01', '2023-01-01T00:00:00Z', NOW(), NOW()),

-- ── KAISER PERMANENTE ────────────────────────────────────────────────────────

('b2c3d4e5-0028-0028-0028-000000000128',
 'KAI-NC', 'KAISER-NCA', 'Kaiser Permanente Northern California', 'Kaiser NCA',
 'PROF', 'Active', 'EDI', 'Standard',
 false, false, false, false, true, false,
 '2016-01-01', NULL, '2024-01-15T08:00:00Z', NOW(), NOW()),

('b2c3d4e5-0029-0029-0029-000000000129',
 'KAI-SC', 'KAISER-SCA', 'Kaiser Permanente Southern California', 'Kaiser SCA',
 'PROF', 'Active', 'EDI', 'Standard',
 false, false, false, false, true, false,
 '2016-01-01', NULL, '2024-01-15T08:00:00Z', NOW(), NOW()),

('b2c3d4e5-0030-0030-0030-000000000130',
 'KAI-NW', 'KAISER-NW', 'Kaiser Permanente Northwest', 'Kaiser NW',
 'PROF', 'Active', 'EDI', 'Standard',
 false, false, false, false, true, false,
 '2017-01-01', NULL, '2024-01-15T08:00:00Z', NOW(), NOW()),

-- ── REGIONAL / MIDMARKET ─────────────────────────────────────────────────────

('b2c3d4e5-0031-0031-0031-000000000131',
 'UPMC-PA', 'UPMC-HLTH', 'UPMC Health Plan Pennsylvania', 'UPMC Health',
 'PROF', 'Active', 'EDI', 'ERA',
 true, false, false, false, true, false,
 '2018-06-01', NULL, '2024-02-20T09:00:00Z', NOW(), NOW()),

('b2c3d4e5-0032-0032-0032-000000000132',
 'GEI-PA', 'GEISINGER', 'Geisinger Health Plan Pennsylvania', 'Geisinger HP',
 'PROF', 'Active', 'EDI', 'Standard',
 true, false, false, false, true, false,
 '2019-01-01', NULL, '2024-01-10T09:00:00Z', NOW(), NOW()),

('b2c3d4e5-0033-0033-0033-000000000133',
 'MED-MN', 'MEDICA-MN', 'Medica Health Plan Minnesota', 'Medica MN',
 'PROF', 'Active', 'EDI', 'Standard',
 true, false, false, false, true, false,
 '2018-01-01', NULL, '2023-11-01T08:00:00Z', NOW(), NOW()),

('b2c3d4e5-0034-0034-0034-000000000134',
 'PRI-MI', 'PRIORITY-MI', 'Priority Health Michigan', 'Priority Health MI',
 'PROF', 'Active', 'EDI', 'Standard',
 true, false, false, false, true, false,
 '2019-06-01', NULL, '2024-01-20T09:00:00Z', NOW(), NOW()),

('b2c3d4e5-0035-0035-0035-000000000135',
 'HAP-MI', 'HAP-MI', 'Health Alliance Plan Michigan', 'HAP Michigan',
 'PROF', 'Active', 'EDI', 'Standard',
 false, false, false, false, true, false,
 '2020-01-01', NULL, '2024-02-01T08:00:00Z', NOW(), NOW()),

('b2c3d4e5-0036-0036-0036-000000000136',
 'MCL-MI', 'MCLAREN-MI', 'McLaren Health Plan Michigan', 'McLaren HP',
 'PROF', 'Active', 'EDI', 'Standard',
 false, false, false, false, true, false,
 '2020-06-01', NULL, '2024-01-10T08:00:00Z', NOW(), NOW()),

('b2c3d4e5-0037-0037-0037-000000000137',
 'MER-MI', 'MERIDIAN-MI', 'Meridian Health Plan Michigan', 'Meridian MI',
 'PROF', 'Active', 'EDI', 'Standard',
 false, false, false, true, false, false,
 '2021-01-01', NULL, '2024-03-01T08:00:00Z', NOW(), NOW()),

('b2c3d4e5-0038-0038-0038-000000000138',
 'TUF-MA', 'TUFTS-MA', 'Tufts Health Plan Massachusetts', 'Tufts HP',
 'PROF', 'Active', 'EDI', 'ERA',
 true, false, false, false, true, false,
 '2017-01-01', NULL, '2024-02-10T09:00:00Z', NOW(), NOW()),

('b2c3d4e5-0039-0039-0039-000000000139',
 'HAR-MA', 'HARVARD-PIL', 'Harvard Pilgrim Health Care', 'Harvard Pilgrim',
 'PROF', 'Active', 'EDI', 'Standard',
 true, false, false, false, true, false,
 '2017-06-01', NULL, '2024-01-15T08:00:00Z', NOW(), NOW()),

('b2c3d4e5-0040-0040-0040-000000000140',
 'CAP-PA', 'CAPITAL-BC', 'Capital BlueCross Pennsylvania', 'Capital BC PA',
 'PROF', 'Active', 'EDI', 'Standard',
 false, false, false, false, true, false,
 '2019-01-01', NULL, '2024-01-05T08:00:00Z', NOW(), NOW()),

-- ── BEHAVIORAL HEALTH ────────────────────────────────────────────────────────

('b2c3d4e5-0041-0041-0041-000000000141',
 'OPT-BH', 'OPTUM-BH', 'Optum Behavioral Health', 'Optum BH',
 'PROF', 'Active', 'EDI', 'Standard',
 false, true, false, false, true, false,
 '2019-01-01', NULL, '2024-04-01T10:00:00Z', NOW(), NOW()),

('b2c3d4e5-0042-0042-0042-000000000142',
 'BCN-BH', 'BEACON-BH', 'Beacon Health Options', 'Beacon Health',
 'PROF', 'Active', 'EDI', 'Standard',
 false, false, false, false, true, false,
 '2018-01-01', NULL, '2023-10-15T09:00:00Z', NOW(), NOW()),

('b2c3d4e5-0043-0043-0043-000000000143',
 'MAG-BH', 'MAGELLAN-BH', 'Magellan Health Behavioral', 'Magellan BH',
 'PROF', 'Active', 'EDI', 'Standard',
 false, false, false, false, true, false,
 '2018-06-01', NULL, '2023-09-01T08:00:00Z', NOW(), NOW()),

('b2c3d4e5-0044-0044-0044-000000000144',
 'EVN-BH', 'EVERNORTH-BH', 'Evernorth Behavioral Health', 'Evernorth BH',
 'PROF', 'Active', 'EDI', 'Standard',
 false, false, false, false, true, false,
 '2021-01-01', NULL, '2024-03-20T09:00:00Z', NOW(), NOW()),

-- ── DENTAL ───────────────────────────────────────────────────────────────────

('b2c3d4e5-0045-0045-0045-000000000145',
 'MET-DEN', 'METLIFE-DEN', 'MetLife Dental', 'MetLife Dental',
 'DENTAL', 'Active', 'EDI', 'Standard',
 false, false, false, false, true, false,
 '2019-01-01', NULL, '2024-01-10T08:00:00Z', NOW(), NOW()),

('b2c3d4e5-0046-0046-0046-000000000146',
 'CIG-DEN', 'CIGNA-DEN', 'Cigna Dental', 'Cigna Dental',
 'DENTAL', 'Active', 'EDI', 'Standard',
 false, false, false, false, true, false,
 '2019-06-01', NULL, '2024-01-10T08:00:00Z', NOW(), NOW()),

('b2c3d4e5-0047-0047-0047-000000000147',
 'AET-DEN', 'AETNA-DEN', 'Aetna Dental', 'Aetna Dental',
 'DENTAL', 'Active', 'EDI', 'Standard',
 false, false, false, false, false, false,
 '2020-01-01', NULL, '2024-02-01T08:00:00Z', NOW(), NOW()),

('b2c3d4e5-0048-0048-0048-000000000148',
 'HUM-DEN', 'HUMANA-DEN', 'Humana Dental', 'Humana Dental',
 'DENTAL', 'Active', 'EDI', 'Standard',
 false, false, false, false, false, false,
 '2020-06-01', NULL, '2024-02-01T08:00:00Z', NOW(), NOW()),

-- ── INACTIVE / LEGACY ────────────────────────────────────────────────────────

('b2c3d4e5-0049-0049-0049-000000000149',
 'BRT-HLT', 'BRIGHT-HLT', 'Bright Health Professional', 'Bright Health',
 'PROF', 'Inactive', 'EDI', 'Standard',
 false, false, false, false, true, false,
 '2020-01-01', '2023-06-30', '2023-06-30T23:59:00Z', NOW(), NOW()),

('b2c3d4e5-0050-0050-0050-000000000150',
 'FRI-HLT', 'FRIDAY-HLT', 'Friday Health Plans', 'Friday Health',
 'PROF', 'Inactive', 'EDI', 'Standard',
 false, false, false, false, false, false,
 '2021-01-01', '2023-09-30', '2023-09-30T23:59:00Z', NOW(), NOW()),

('b2c3d4e5-0051-0051-0051-000000000151',
 'WLP', 'WELLPOINT', 'WellPoint Inc', 'WellPoint',
 'PROF', 'Inactive', 'EDI', 'Standard',
 true, false, false, false, true, false,
 '2014-01-01', '2021-12-31', '2021-12-31T23:59:00Z', NOW(), NOW()),

-- ── CVS / AETNA COMBINED ─────────────────────────────────────────────────────

('b2c3d4e5-0052-0052-0052-000000000152',
 'CVS-AET', 'CVS-AETNA', 'CVS Health Aetna Commercial', 'CVS Aetna',
 'PROF', 'Active', 'EDI', 'ERA',
 true, false, false, false, true, false,
 '2022-01-01', NULL, '2024-05-01T10:00:00Z', NOW(), NOW()),

('b2c3d4e5-0053-0053-0053-000000000153',
 'CVS-AET-I', 'CVS-AETNA-INST', 'CVS Health Aetna Institutional', 'CVS Aetna Inst',
 'INST', 'Active', 'EDI', 'ERA',
 true, false, false, false, true, false,
 '2022-01-01', NULL, '2024-05-01T10:00:00Z', NOW(), NOW()),

-- ── VISION ───────────────────────────────────────────────────────────────────

('b2c3d4e5-0054-0054-0054-000000000154',
 'VSP-VIS', 'VSP-VISION', 'VSP Vision Care', 'VSP Vision',
 'PROF', 'Active', 'EDI', 'Standard',
 false, false, false, false, true, false,
 '2019-01-01', NULL, '2023-11-01T08:00:00Z', NOW(), NOW()),

('b2c3d4e5-0055-0055-0055-000000000155',
 'DAV-VIS', 'DAVIS-VIS', 'Davis Vision', 'Davis Vision',
 'PROF', 'Active', 'EDI', 'Standard',
 false, false, false, false, false, false,
 '2020-01-01', NULL, '2023-10-01T08:00:00Z', NOW(), NOW());


-- States for Anthem plans
INSERT INTO payer_states (payer_id, label, value) VALUES
('b2c3d4e5-0001-0001-0001-000000000101', 'Ohio', 'OH'),
('b2c3d4e5-0002-0002-0002-000000000102', 'Indiana', 'IN'),
('b2c3d4e5-0003-0003-0003-000000000103', 'Virginia', 'VA'),
('b2c3d4e5-0004-0004-0004-000000000104', 'Georgia', 'GA'),
('b2c3d4e5-0005-0005-0005-000000000105', 'Connecticut', 'CT'),
('b2c3d4e5-0006-0006-0006-000000000106', 'New York', 'NY'),
('b2c3d4e5-0007-0007-0007-000000000107', 'New York', 'NY'),
('b2c3d4e5-0008-0008-0008-000000000108', 'New York', 'NY'),
('b2c3d4e5-0009-0009-0009-000000000109', 'Pennsylvania', 'PA'),
('b2c3d4e5-0010-0010-0010-000000000110', 'West Virginia', 'WV'),
('b2c3d4e5-0011-0011-0011-000000000111', 'Maryland', 'MD'),
('b2c3d4e5-0011-0011-0011-000000000111', 'Washington DC', 'DC'),
('b2c3d4e5-0012-0012-0012-000000000112', 'Pennsylvania', 'PA'),
('b2c3d4e5-0013-0013-0013-000000000113', 'North Carolina', 'NC'),
('b2c3d4e5-0014-0014-0014-000000000114', 'Massachusetts', 'MA'),
('b2c3d4e5-0015-0015-0015-000000000115', 'Minnesota', 'MN'),
('b2c3d4e5-0028-0028-0028-000000000128', 'California', 'CA'),
('b2c3d4e5-0029-0029-0029-000000000129', 'California', 'CA'),
('b2c3d4e5-0030-0030-0030-000000000130', 'Oregon', 'OR'),
('b2c3d4e5-0030-0030-0030-000000000130', 'Washington', 'WA'),
('b2c3d4e5-0031-0031-0031-000000000131', 'Pennsylvania', 'PA'),
('b2c3d4e5-0032-0032-0032-000000000132', 'Pennsylvania', 'PA'),
('b2c3d4e5-0033-0033-0033-000000000133', 'Minnesota', 'MN'),
('b2c3d4e5-0034-0034-0034-000000000134', 'Michigan', 'MI'),
('b2c3d4e5-0035-0035-0035-000000000135', 'Michigan', 'MI'),
('b2c3d4e5-0036-0036-0036-000000000136', 'Michigan', 'MI'),
('b2c3d4e5-0037-0037-0037-000000000137', 'Michigan', 'MI'),
('b2c3d4e5-0038-0038-0038-000000000138', 'Massachusetts', 'MA'),
('b2c3d4e5-0039-0039-0039-000000000139', 'Massachusetts', 'MA'),
('b2c3d4e5-0040-0040-0040-000000000140', 'Pennsylvania', 'PA');
