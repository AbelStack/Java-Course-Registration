-- ================================================================
-- CREATE TEST STUDENT SCRIPT
-- Creates a test student account for testing the Student Portal
-- ================================================================

USE javapro;

-- Insert test student into students_v2
INSERT INTO students_v2 (student_id, name, email, department_id, year_level, gpa)
VALUES ('CS-2026-001', 'Test Student', 'student@test.com', 1, 1, 3.50);

-- Get the student_v2 ID
SET @student_v2_id = LAST_INSERT_ID();

-- Create user account (auto-approved)
INSERT INTO users (username, password, role, full_name, email, department, approved, student_id)
VALUES ('CS-2026-001', 'student123', 'STUDENT', 'Test Student', 'student@test.com', 'Computer Science', TRUE, @student_v2_id);

-- Verify creation
SELECT 'Test student created successfully!' AS status;

SELECT 'Student Record:' AS info;
SELECT * FROM students_v2 WHERE student_id = 'CS-2026-001';

SELECT 'User Account:' AS info;
SELECT id, username, role, full_name, email, department, approved FROM users WHERE username = 'CS-2026-001';

-- Login credentials
SELECT '==================================================' AS separator;
SELECT 'LOGIN CREDENTIALS' AS info;
SELECT '==================================================' AS separator;
SELECT 'Username: CS-2026-001' AS credential;
SELECT 'Password: student123' AS credential;
SELECT 'Role: STUDENT' AS credential;
SELECT '==================================================' AS separator;
