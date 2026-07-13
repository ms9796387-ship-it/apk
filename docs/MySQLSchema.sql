-- MySQL Database Schema: CertiChain Authentication System
-- File: docs/MySQLSchema.sql
-- Part of Final Year Graduation Project:
-- "Blockchain-Based Authentication of Academic Certifications in Remote Learning Systems"

CREATE DATABASE IF NOT EXISTS certichain_db;
USE certichain_db;

-- 1. TABLE: Users (Stores all role-based accounts)
CREATE TABLE IF NOT EXISTS Users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'INSTITUTION', 'STUDENT', 'EMPLOYER') NOT NULL DEFAULT 'STUDENT',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_email (email),
    INDEX idx_user_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. TABLE: Institutions (Profile details for academic bodies)
CREATE TABLE IF NOT EXISTS Institutions (
    institution_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    institution_name VARCHAR(255) NOT NULL,
    accreditation_status BOOLEAN NOT NULL DEFAULT FALSE, -- Approved by ADMIN
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    INDEX idx_institution_name (institution_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. TABLE: Students (Profile details for matriculated students)
CREATE TABLE IF NOT EXISTS Students (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    student_id_card VARCHAR(50) NOT NULL UNIQUE, -- University Roll/Registration Number
    institution_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (institution_id) REFERENCES Institutions(institution_id) ON DELETE RESTRICT,
    INDEX idx_student_card (student_id_card)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. TABLE: Certificates (Digital records corresponding to on-chain hashes)
CREATE TABLE IF NOT EXISTS Certificates (
    certificate_id VARCHAR(100) PRIMARY KEY, -- Unique ID e.g. CERT-2026-90412
    student_id INT NOT NULL,
    institution_id INT NOT NULL,
    course_name VARCHAR(255) NOT NULL,
    issue_date DATE NOT NULL,
    certificate_hash CHAR(64) NOT NULL UNIQUE, -- SHA-256 Digest of Certificate PDF
    blockchain_tx_hash CHAR(66) NOT NULL UNIQUE, -- Eth Transaction Hash (0x...)
    certificate_status ENUM('VALID', 'REVOKED') NOT NULL DEFAULT 'VALID',
    qr_code_path VARCHAR(512) NULL, -- Path to stored QR code image
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES Students(student_id) ON DELETE RESTRICT,
    FOREIGN KEY (institution_id) REFERENCES Institutions(institution_id) ON DELETE RESTRICT,
    INDEX idx_cert_hash (certificate_hash),
    INDEX idx_cert_tx (blockchain_tx_hash),
    INDEX idx_cert_status (certificate_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. TABLE: Employers (Verifier profiles)
CREATE TABLE IF NOT EXISTS Employers (
    employer_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    company_name VARCHAR(255) NOT NULL,
    contact_person VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. TABLE: VerificationRecords (Logs verifications performed by Employers)
CREATE TABLE IF NOT EXISTS VerificationRecords (
    verification_id INT AUTO_INCREMENT PRIMARY KEY,
    employer_id INT NOT NULL,
    certificate_id VARCHAR(100) NULL,
    queried_hash CHAR(64) NOT NULL,
    verification_result ENUM('VERIFIED', 'INVALID', 'REVOKED') NOT NULL,
    verification_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employer_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (certificate_id) REFERENCES Certificates(certificate_id) ON DELETE SET NULL,
    INDEX idx_verify_hash (queried_hash)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. TABLE: AuditLogs (Security logs for administrative auditing)
CREATE TABLE IF NOT EXISTS AuditLogs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NULL,
    action VARCHAR(100) NOT NULL, -- e.g. "AUTH_LOGIN", "ISSUE_CERT", "REVOKE_CERT"
    description TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE SET NULL,
    INDEX idx_audit_action (action),
    INDEX idx_audit_time (timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==========================================
-- SAMPLE DATA POPULATION (For Demonstration)
-- ==========================================

-- Populate Admin User (Password is hashed in application layer)
INSERT INTO Users (user_id, full_name, email, password_hash, role) 
VALUES (1, 'Admin Administrator', 'admin@edu.com', '$2b$12$6t6L5jHlyX9O7Wp3jR9mSuYmF7eU8W/C4f5v/Zp.G71o.U4E9a1qO', 'ADMIN');

-- Populate Academic Institution User
INSERT INTO Users (user_id, full_name, email, password_hash, role) 
VALUES (2, 'State Technical University Registrar', 'registrar@stu.edu', '$2b$12$yT6fE2rD8gM8z9P0qR1sTuWpXZmK3a5f8g7h9j0k1l2m3n4o5p6q', 'INSTITUTION');

INSERT INTO Institutions (institution_id, user_id, institution_name, accreditation_status)
VALUES (1, 2, 'State Technical University', TRUE);

-- Populate Student User
INSERT INTO Users (user_id, full_name, email, password_hash, role) 
VALUES (3, 'John Doe', 'student@stu.edu', '$2b$12$vD5gE2rD8gM8z9P0qR1sTuWpXZmK3a5f8g7h9j0k1l2m3n4o5p6q', 'STUDENT');

INSERT INTO Students (student_id, user_id, student_id_card, institution_id)
VALUES (1, 3, 'STU-2023-88941', 1);

-- Populate Employer User
INSERT INTO Users (user_id, full_name, email, password_hash, role) 
VALUES (4, 'Jane Smith (HR Google)', 'hr@google.com', '$2b$12$kD5gE2rD8gM8z9P0qR1sTuWpXZmK3a5f8g7h9j0k1l2m3n4o5p6q', 'EMPLOYER');

INSERT INTO Employers (employer_id, user_id, company_name, contact_person)
VALUES (1, 4, 'Google LLC', 'Jane Smith');

-- Populate Sample Certificate Hash
INSERT INTO Certificates (certificate_id, student_id, institution_id, course_name, issue_date, certificate_hash, blockchain_tx_hash, certificate_status, qr_code_path)
VALUES (
    'STU-BSC-2026-001', 
    1, 
    1, 
    'Bachelor of Science in Computer Science', 
    '2026-05-15', 
    'f3b49e29a1b1836a940fcb28c049ee981a8bdf1da4a04d3e58b16e8647c210d4', -- Sample SHA-256 Hash
    '0x2e0f9b6c04f98274112e45da812ff5e13d1000f6bca82f42dcd1a1829efab304', -- Sepolia Mock Transaction
    'VALID',
    '/media/qrcodes/STU-BSC-2026-001.png'
);

-- Populate Sample Audit Logs
INSERT INTO AuditLogs (user_id, action, description) VALUES (2, 'ISSUE_CERTIFICATE', 'Issued certificate STU-BSC-2026-001 for student John Doe');
INSERT INTO AuditLogs (user_id, action, description) VALUES (4, 'VERIFY_CERTIFICATE', 'Verified authentic certificate STU-BSC-2026-001 using QR scan');
