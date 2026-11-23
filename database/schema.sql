-- ============================================================
-- schema.sql — Jansetu Database Setup Script
--
-- Run this file ONCE in MySQL before starting the Java app.
-- It creates the database, tables, and sample data.
--
-- How to run:
--   Option A (MySQL command line):
--     mysql -u root -p < database/schema.sql
--
--   Option B (MySQL Workbench):
--     Open this file → click the lightning bolt (Execute) button
--
--   Option C (MySQL command line, step by step):
--     mysql -u root -p
--     source /full/path/to/database/schema.sql
-- ============================================================


-- ============================================================
-- 1. Create the database (safe — won't fail if it exists)
-- ============================================================
CREATE DATABASE IF NOT EXISTS jansetu;

-- Switch to the jansetu database for all commands below
USE jansetu;


-- ============================================================
-- 2. Create the 'users' table
--    Stores details of people who want scheme recommendations
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    user_id         INT AUTO_INCREMENT PRIMARY KEY,  -- unique ID, auto-increases
    name            VARCHAR(100) NOT NULL,
    age             INT NOT NULL,
    state           VARCHAR(100) NOT NULL,
    profession      VARCHAR(100) NOT NULL,
    income          DECIMAL(12, 2) NOT NULL,          -- annual income in rupees
    education_level VARCHAR(50)  NOT NULL
);


-- ============================================================
-- 3. Create the 'schemes' table
--    Stores all government schemes in the system
-- ============================================================
CREATE TABLE IF NOT EXISTS schemes (
    scheme_id        INT AUTO_INCREMENT PRIMARY KEY,
    scheme_name      VARCHAR(200) NOT NULL,
    description      TEXT,
    min_age          INT  DEFAULT 0,
    max_age          INT  DEFAULT 99,
    profession       VARCHAR(100) DEFAULT 'Any',      -- 'Any' means open to all
    state            VARCHAR(100) DEFAULT 'All',      -- 'All' means central scheme
    income_limit     DECIMAL(12, 2) DEFAULT 0,        -- 0 means no income limit
    education_level  VARCHAR(50)  DEFAULT 'Any',
    category         VARCHAR(100) NOT NULL,
    benefits         TEXT,
    application_link VARCHAR(500)
);


-- ============================================================
-- 4. Sample schemes (5–8 realistic records)
--    These are based on real Indian government schemes,
--    simplified for a college project.
-- ============================================================

-- Clear existing data first so running this script twice is safe
DELETE FROM schemes;

-- Reset the auto-increment counter to 1
ALTER TABLE schemes AUTO_INCREMENT = 1;

INSERT INTO schemes
    (scheme_name, description, min_age, max_age, profession, state,
     income_limit, education_level, category, benefits, application_link)
VALUES

-- 1. PM-KISAN (central agriculture scheme)
(
    'PM-KISAN (Pradhan Mantri Kisan Samman Nidhi)',
    'Provides income support of Rs.6000 per year to small and marginal farmers.',
    18, 70,
    'Farmer', 'All',
    200000,   -- income limit: Rs.2 lakh per year
    'Any',
    'Agriculture',
    'Rs.6000 per year in 3 instalments of Rs.2000 directly to bank account.',
    'https://pmkisan.gov.in'
),

-- 2. PM Awas Yojana - Gramin (housing for rural poor)
(
    'PM Awas Yojana - Gramin',
    'Financial assistance to rural households for constructing a pucca house.',
    18, 99,
    'Any', 'All',
    0,        -- no income limit specified
    'Any',
    'Housing',
    'Rs.1.2 lakh in plains or Rs.1.3 lakh in hilly areas for house construction.',
    'https://pmayg.nic.in'
),

-- 3. National Scholarship Portal (students)
(
    'National Scholarship Portal - Central Schemes',
    'Scholarships for minority, OBC, and economically weaker students to continue education.',
    15, 30,
    'Student', 'All',
    250000,   -- income limit: Rs.2.5 lakh per year
    '10th Pass',
    'Education',
    'Maintenance allowance, tuition fee reimbursement, and book grants.',
    'https://scholarships.gov.in'
),

-- 4. Ayushman Bharat (health insurance)
(
    'Ayushman Bharat - PM Jan Arogya Yojana',
    'Health insurance coverage of Rs.5 lakh per family per year for hospitalisation.',
    0, 99,
    'Any', 'All',
    0,        -- targeted at BPL / economically weaker families
    'Any',
    'Health',
    'Cashless hospitalisation up to Rs.5 lakh per year at empanelled hospitals.',
    'https://pmjay.gov.in'
),

-- 5. Mudra Yojana (small business loan)
(
    'PM Mudra Yojana (PMMY)',
    'Collateral-free loans for non-farm small/micro enterprises.',
    18, 65,
    'Self-Employed', 'All',
    0,        -- no income limit
    'Any',
    'Business',
    'Loans from Rs.50,000 up to Rs.10 lakh under Shishu, Kishor, and Tarun categories.',
    'https://mudra.org.in'
),

-- 6. Ladli Behna Yojana - Madhya Pradesh (women, state-specific)
(
    'Ladli Behna Yojana',
    'Monthly financial assistance to married women of Madhya Pradesh to empower them.',
    21, 60,
    'Any', 'Madhya Pradesh',
    250000,   -- family income limit: Rs.2.5 lakh per year
    'Any',
    'Women Welfare',
    'Rs.1250 per month directly into the woman''s bank account.',
    'https://cmladlibahna.mp.gov.in'
),

-- 7. Kisan Credit Card
(
    'Kisan Credit Card (KCC)',
    'Provides farmers with timely credit for agricultural needs at low interest rates.',
    18, 75,
    'Farmer', 'All',
    0,
    'Any',
    'Agriculture',
    'Short-term credit up to Rs.3 lakh at 4% interest per year for crop and allied activities.',
    'https://www.nabard.org/content.aspx?id=24'
),

-- 8. Skill India (PMKVY) for youth
(
    'Pradhan Mantri Kaushal Vikas Yojana (PMKVY)',
    'Free skill training and certification for school/college dropouts and unemployed youth.',
    15, 35,
    'Any', 'All',
    0,
    'Any',
    'Skill Development',
    'Free short-term skill courses, government-recognised certification, and monetary reward.',
    'https://www.pmkvyofficial.org'
);


-- ============================================================
-- Quick check — run this to see what was inserted
-- ============================================================
SELECT scheme_id, scheme_name, category, state FROM schemes;
