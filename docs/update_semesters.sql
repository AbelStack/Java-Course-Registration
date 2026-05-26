-- ================================================================
-- UPDATE SEMESTERS SCRIPT
-- Changes Fall/Spring/Summer to Semester I/Semester II
-- ================================================================

USE javapro;

-- Update existing semester names and codes
UPDATE semesters SET semester_code = 'SEM1', semester_name = 'Semester I' WHERE semester_code = 'FALL';
UPDATE semesters SET semester_code = 'SEM2', semester_name = 'Semester II' WHERE semester_code = 'SPRING';

-- Delete Summer semester if it exists
DELETE FROM semesters WHERE semester_code = 'SUMMER';

-- Update registration period names
UPDATE registration_periods SET period_name = 'Semester I 2025 Registration' WHERE period_name LIKE '%Fall%';
UPDATE registration_periods SET period_name = 'Semester II 2025 Registration' WHERE period_name LIKE '%Spring%';

-- Verify changes
SELECT 'Updated Semesters:' AS status;
SELECT id, semester_code, semester_name, start_date, end_date, is_current FROM semesters;

SELECT 'Updated Registration Periods:' AS status;
SELECT id, period_name, start_date, end_date, is_active FROM registration_periods;

SELECT 'Update completed!' AS status;
