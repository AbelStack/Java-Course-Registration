-- ============================================================================
-- Default Admin User
-- Course Registration System - RBAC Implementation
-- ============================================================================
-- This script is for reference only. The default admin user is automatically
-- created by DatabaseInitializer.java during system initialization.
-- 
-- All other users (Department Heads, Students, Instructors) should be added
-- dynamically through the admin dashboard interface.
-- ============================================================================

-- Note: Passwords are stored in plain text for development purposes only
-- In production, passwords should be hashed using BCrypt or similar

-- ============================================================================
-- DEFAULT ADMIN USER (Auto-created by DatabaseInitializer)
-- ============================================================================

-- Default admin (automatically created by DatabaseInitializer.java)
-- Username: admin
-- Password: admin123
-- Role: ADMIN
-- Full Name: System Administrator
-- Email: admin@university.edu

-- This user is created automatically when the application starts.
-- Use this account to log in and create additional users through the UI.

-- ============================================================================
-- VERIFICATION QUERIES
-- ============================================================================

-- Count users by role
-- SELECT role, COUNT(*) as count FROM users GROUP BY role;

-- List all admins
-- SELECT id, username, full_name, email FROM users WHERE role = 'ADMIN';

-- List all department heads
-- SELECT id, username, full_name, email, department FROM users WHERE role = 'DEPARTMENT_HEAD';

-- List approved students
-- SELECT id, username, full_name, email, department FROM users WHERE role = 'STUDENT' AND approved = TRUE;

-- List pending students
-- SELECT id, username, full_name, email, department FROM users WHERE role = 'STUDENT' AND approved = FALSE;

-- ============================================================================
-- DEFAULT CREDENTIALS
-- ============================================================================

/*
DEFAULT ADMIN ACCOUNT:
---------------------
Username: admin
Password: admin123
Role: ADMIN
Department: N/A

IMPORTANT NOTES:
---------------
1. This is the ONLY hardcoded user in the system
2. All other users must be created through the admin dashboard
3. Admin can create:
   - Additional admin accounts
   - Department head accounts
   - Student accounts (or approve student registrations)
   - Instructor accounts
4. Change the default admin password after first login for security
5. Students can self-register but require admin approval before accessing the system
*/
