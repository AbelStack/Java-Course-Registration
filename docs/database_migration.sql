-- ================================================================
-- DATABASE MIGRATION SCRIPT
-- Course Registration System
-- ================================================================
-- This script will update your existing database to the latest schema
-- Run this if you have existing data that's not showing up
-- ================================================================

USE javapro;

-- Add department column to students table if it doesn't exist
-- Check if column exists first
SET @dbname = 'javapro';
SET @tablename = 'students';
SET @columnname = 'department';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      (table_name = @tablename)
      AND (table_schema = @dbname)
      AND (column_name = @columnname)
  ) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' VARCHAR(100) NOT NULL DEFAULT ''Computer Science''')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- Add department column to users table if it doesn't exist
SET @tablename = 'users';
SET @columnname = 'department';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      (table_name = @tablename)
      AND (table_schema = @dbname)
      AND (column_name = @columnname)
  ) > 0,
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' VARCHAR(100) NULL')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- Create departments table if it doesn't exist
CREATE TABLE IF NOT EXISTS departments (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    code        VARCHAR(10) NOT NULL UNIQUE,
    name        VARCHAR(100) NOT NULL,
    head_id     INT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (head_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Seed departments if empty
INSERT IGNORE INTO departments (code, name) VALUES
('CS', 'Computer Science'),
('SWE', 'Software Engineering'),
('IT', 'Information Technology'),
('EE', 'Electrical Engineering'),
('ME', 'Mechanical Engineering');

-- Create academic_years table if it doesn't exist
CREATE TABLE IF NOT EXISTS academic_years (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    year_code   VARCHAR(10) NOT NULL UNIQUE,
    start_date  DATE NOT NULL,
    end_date    DATE NOT NULL,
    is_current  BOOLEAN DEFAULT FALSE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Seed academic year if empty
INSERT IGNORE INTO academic_years (year_code, start_date, end_date, is_current) VALUES
('2024-2025', '2024-09-01', '2025-06-30', TRUE),
('2025-2026', '2025-09-01', '2026-06-30', FALSE),
('2026-2027', '2026-09-01', '2027-06-30', FALSE);

-- Create semesters table if it doesn't exist
CREATE TABLE IF NOT EXISTS semesters (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    academic_year_id INT NOT NULL,
    semester_code   VARCHAR(20) NOT NULL,
    semester_name   VARCHAR(50) NOT NULL,
    start_date      DATE NOT NULL,
    end_date        DATE NOT NULL,
    is_current      BOOLEAN DEFAULT FALSE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_semester (academic_year_id, semester_code),
    FOREIGN KEY (academic_year_id) REFERENCES academic_years(id) ON DELETE CASCADE
);

-- Seed semesters if empty
INSERT IGNORE INTO semesters (academic_year_id, semester_code, semester_name, start_date, end_date, is_current) VALUES
(1, 'SEM1', 'Semester I', '2024-09-01', '2025-01-15', FALSE),
(1, 'SEM2', 'Semester II', '2025-01-16', '2025-06-30', TRUE);

-- Create registration_periods table if it doesn't exist
CREATE TABLE IF NOT EXISTS registration_periods (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    semester_id     INT NOT NULL,
    period_name     VARCHAR(100) NOT NULL,
    start_date      DATETIME NOT NULL,
    end_date        DATETIME NOT NULL,
    is_active       BOOLEAN DEFAULT TRUE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE CASCADE
);

-- Seed registration period if empty
INSERT IGNORE INTO registration_periods (semester_id, period_name, start_date, end_date, is_active) VALUES
(2, 'Semester II 2025 Registration', '2024-11-01 00:00:00', '2025-01-10 23:59:59', TRUE);

-- ================================================================
-- MIGRATE EXISTING STUDENTS TO STUDENTS_V2
-- ================================================================
INSERT INTO students_v2 (student_id, name, email, department_id, year_level)
SELECT 
    CONCAT((SELECT code FROM departments WHERE name = s.department LIMIT 1), '-2026-', LPAD(s.id, 3, '0')),
    s.name,
    s.email,
    (SELECT id FROM departments WHERE name = s.department LIMIT 1),
    1
FROM students s
WHERE NOT EXISTS (SELECT 1 FROM students_v2 WHERE email = s.email);

-- ================================================================
-- VERIFY DATA
-- ================================================================

SELECT 'Checking existing data...' AS status;

SELECT COUNT(*) AS total_users, 
       SUM(CASE WHEN role = 'STUDENT' THEN 1 ELSE 0 END) AS students,
       SUM(CASE WHEN role = 'ADMIN' THEN 1 ELSE 0 END) AS admins,
       SUM(CASE WHEN role = 'DEPARTMENT_HEAD' THEN 1 ELSE 0 END) AS dept_heads
FROM users;

SELECT COUNT(*) AS total_students_legacy FROM students;
SELECT COUNT(*) AS total_students_v2 FROM students_v2;
SELECT COUNT(*) AS total_courses FROM courses;
SELECT COUNT(*) AS total_instructors FROM instructors;
SELECT COUNT(*) AS total_departments FROM departments;

SELECT 'Migration completed!' AS status;
