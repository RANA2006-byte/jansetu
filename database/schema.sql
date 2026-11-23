-- ============================================================
-- schema.sql — Jansetu Database Setup Script
--
-- Run this file ONCE in MySQL before starting the Java app.
-- It creates the database, all tables, and sample data.
-- Safe to re-run: uses IF NOT EXISTS everywhere.
--
-- How to run:
--   Option A (MySQL command line):
--     mysql -u root -p < database/schema.sql
--
--   Option B (MySQL Workbench):
--     Open this file → click the lightning bolt (Execute All) button
--
--   Option C (MySQL command line, step by step):
--     mysql -u root -p
--     source C:/full/path/to/database/schema.sql
-- ============================================================


-- ============================================================
-- 1. Create the database
-- ============================================================
CREATE DATABASE IF NOT EXISTS jansetu;
USE jansetu;


-- ============================================================
-- 2. users table  (applicants who check eligibility)
--    Added gender + marital_status columns required by
--    UserDAO and RecommendationService.
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    user_id         INT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    age             INT NOT NULL,
    state           VARCHAR(100) NOT NULL,
    profession      VARCHAR(100) NOT NULL,
    income          DECIMAL(12,2) NOT NULL,
    education_level VARCHAR(50)  NOT NULL,
    gender          VARCHAR(20)  NOT NULL DEFAULT 'Other',
    marital_status  VARCHAR(20)  NOT NULL DEFAULT 'Unmarried'
);

-- Add columns if the table already existed without them
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS gender         VARCHAR(20) NOT NULL DEFAULT 'Other',
    ADD COLUMN IF NOT EXISTS marital_status VARCHAR(20) NOT NULL DEFAULT 'Unmarried';


-- ============================================================
-- 3. schemes table
--    Added eligible_gender + eligible_marital_status columns
--    required by SchemeDAO and RecommendationService.
-- ============================================================
CREATE TABLE IF NOT EXISTS schemes (
    scheme_id               INT AUTO_INCREMENT PRIMARY KEY,
    scheme_name             VARCHAR(200) NOT NULL,
    description             TEXT,
    min_age                 INT           DEFAULT 0,
    max_age                 INT           DEFAULT 99,
    profession              VARCHAR(100)  DEFAULT 'Any',
    state                   VARCHAR(100)  DEFAULT 'All',
    income_limit            DECIMAL(12,2) DEFAULT 0,
    education_level         VARCHAR(50)   DEFAULT 'Any',
    category                VARCHAR(100) NOT NULL,
    benefits                TEXT,
    application_link        VARCHAR(500),
    eligible_gender         VARCHAR(20)   DEFAULT 'Any',
    eligible_marital_status VARCHAR(20)   DEFAULT 'Any'
);

-- Add columns if the table already existed without them
ALTER TABLE schemes
    ADD COLUMN IF NOT EXISTS eligible_gender         VARCHAR(20) DEFAULT 'Any',
    ADD COLUMN IF NOT EXISTS eligible_marital_status VARCHAR(20) DEFAULT 'Any';


-- ============================================================
-- 4. accounts table  (registered user accounts)
-- ============================================================
CREATE TABLE IF NOT EXISTS accounts (
    account_id    INT AUTO_INCREMENT PRIMARY KEY,
    full_name     VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(512) NOT NULL,
    password_salt VARCHAR(100) NOT NULL,
    created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);


-- ============================================================
-- 5. user_sessions table  (server-side session management)
--    The browser holds the raw token in an HttpOnly cookie.
--    Only the SHA-256 hash is stored here — never the raw token.
-- ============================================================
CREATE TABLE IF NOT EXISTS user_sessions (
    session_id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT NOT NULL,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    expires_at DATETIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE
);


-- ============================================================
-- 6. Sample schemes  (clear + re-insert each run)
-- ============================================================
DELETE FROM schemes;
ALTER TABLE schemes AUTO_INCREMENT = 1;

INSERT INTO schemes
    (scheme_name, description, min_age, max_age, profession, state,
     income_limit, education_level, category, benefits, application_link,
     eligible_gender, eligible_marital_status)
VALUES

-- 1. PM-KISAN
(
    'PM-KISAN (Pradhan Mantri Kisan Samman Nidhi)',
    'Provides income support of Rs.6000 per year to small and marginal farmers.',
    18, 70, 'Farmer', 'All', 200000, 'Any', 'Agriculture',
    'Rs.6000 per year in 3 instalments of Rs.2000 directly to bank account.',
    'https://pmkisan.gov.in', 'Any', 'Any'
),

-- 2. PM Awas Yojana - Gramin
(
    'PM Awas Yojana - Gramin',
    'Financial assistance to rural households for constructing a pucca house.',
    18, 99, 'Any', 'All', 0, 'Any', 'Housing',
    'Rs.1.2 lakh in plains or Rs.1.3 lakh in hilly areas for house construction.',
    'https://pmayg.nic.in', 'Any', 'Any'
),

-- 3. National Scholarship Portal
(
    'National Scholarship Portal - Central Schemes',
    'Scholarships for minority, OBC, and economically weaker students to continue education.',
    15, 30, 'Student', 'All', 250000, '10th Pass', 'Education',
    'Maintenance allowance, tuition fee reimbursement, and book grants.',
    'https://scholarships.gov.in', 'Any', 'Any'
),

-- 4. Ayushman Bharat
(
    'Ayushman Bharat - PM Jan Arogya Yojana',
    'Health insurance coverage of Rs.5 lakh per family per year for hospitalisation.',
    0, 99, 'Any', 'All', 0, 'Any', 'Health',
    'Cashless hospitalisation up to Rs.5 lakh per year at empanelled hospitals.',
    'https://pmjay.gov.in', 'Any', 'Any'
),

-- 5. PM Mudra Yojana
(
    'PM Mudra Yojana (PMMY)',
    'Collateral-free loans for non-farm small/micro enterprises.',
    18, 65, 'Business', 'All', 0, 'Any', 'Business',
    'Loans from Rs.50,000 up to Rs.10 lakh under Shishu, Kishor, and Tarun categories.',
    'https://mudra.org.in', 'Any', 'Any'
),

-- 6. Ladli Behna Yojana (women only, MP)
(
    'Ladli Behna Yojana',
    'Monthly financial assistance to married women of Madhya Pradesh to empower them.',
    21, 60, 'Any', 'Madhya Pradesh', 250000, 'Any', 'Women Welfare',
    'Rs.1250 per month directly into the woman''s bank account.',
    'https://cmladlibahna.mp.gov.in', 'Female', 'Married'
),

-- 7. Kisan Credit Card
(
    'Kisan Credit Card (KCC)',
    'Provides farmers with timely credit for agricultural needs at low interest rates.',
    18, 75, 'Farmer', 'All', 0, 'Any', 'Agriculture',
    'Short-term credit up to Rs.3 lakh at 4% interest per year for crop and allied activities.',
    'https://www.nabard.org/content.aspx?id=24', 'Any', 'Any'
),

-- 8. PMKVY Skill India
(
    'Pradhan Mantri Kaushal Vikas Yojana (PMKVY)',
    'Free skill training and certification for school/college dropouts and unemployed youth.',
    15, 35, 'Any', 'All', 0, 'Any', 'Skill Development',
    'Free short-term skill courses, government-recognised certification, and monetary reward.',
    'https://www.pmkvyofficial.org', 'Any', 'Any'
);


-- ============================================================
-- Quick sanity check
-- ============================================================
SELECT scheme_id, scheme_name, category, eligible_gender FROM schemes;

