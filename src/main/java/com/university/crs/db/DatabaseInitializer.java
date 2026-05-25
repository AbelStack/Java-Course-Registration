package com.university.crs.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database Initializer - Creates all tables with enhanced schema and seed data
 */
public class DatabaseInitializer {

    public static void initialize() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            System.out.println("=================================================");
            System.out.println("  Initializing Database Schema");
            System.out.println("=================================================");
            System.out.println();

            // ================================================================
            // USERS TABLE
            // ================================================================
            System.out.println("Creating users table...");
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users (
                    id          INT AUTO_INCREMENT PRIMARY KEY,
                    username    VARCHAR(50) NOT NULL UNIQUE,
                    password    VARCHAR(255) NOT NULL,
                    role        ENUM('ADMIN', 'DEPARTMENT_HEAD', 'STUDENT') NOT NULL,
                    full_name   VARCHAR(100) NOT NULL,
                    email       VARCHAR(100) NOT NULL UNIQUE,
                    department  VARCHAR(100) NULL,
                    student_id  INT NULL,
                    approved    BOOLEAN DEFAULT FALSE,
                    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
            """);
            System.out.println("✓ Users table created");

            // ================================================================
            // SEED DEFAULT ADMIN USER
            // ================================================================
            System.out.println("Creating default admin user...");
            stmt.executeUpdate("""
                INSERT IGNORE INTO users (username, password, role, full_name, email, department, approved)
                VALUES ('admin', 'admin123', 'ADMIN', 'System Administrator', 'admin@university.edu', NULL, TRUE)
            """);
            System.out.println("✓ Default admin user created");
            System.out.println("   Username: admin");
            System.out.println("   Password: admin123");

            // ================================================================
            // DEPARTMENTS TABLE
            // ================================================================
            System.out.println("Creating departments table...");
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS departments (
                    id          INT AUTO_INCREMENT PRIMARY KEY,
                    code        VARCHAR(10) NOT NULL UNIQUE,
                    name        VARCHAR(100) NOT NULL,
                    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);
            System.out.println("✓ Departments table created");

            // Seed departments
            System.out.println("Seeding departments...");
            stmt.executeUpdate("""
                INSERT IGNORE INTO departments (code, name) VALUES
                ('CS', 'Computer Science'),
                ('EE', 'Electrical Engineering'),
                ('ME', 'Mechanical Engineering'),
                ('MATH', 'Mathematics'),
                ('PHYS', 'Physics')
            """);
            System.out.println("✓ 5 departments seeded");

            // ================================================================
            // ACADEMIC YEARS TABLE
            // ================================================================
            System.out.println("Creating academic_years table...");
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS academic_years (
                    id          INT AUTO_INCREMENT PRIMARY KEY,
                    year_code   VARCHAR(10) NOT NULL UNIQUE,
                    start_date  DATE NOT NULL,
                    end_date    DATE NOT NULL,
                    is_current  BOOLEAN DEFAULT FALSE,
                    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);
            System.out.println("✓ Academic years table created");

            // Seed academic year
            System.out.println("Seeding academic year...");
            stmt.executeUpdate("""
                INSERT IGNORE INTO academic_years (year_code, start_date, end_date, is_current) VALUES
                ('2024-2025', '2024-09-01', '2025-06-30', TRUE)
            """);
            System.out.println("✓ Academic year 2024-2025 seeded");

            // ================================================================
            // SEMESTERS TABLE
            // ================================================================
            System.out.println("Creating semesters table...");
            stmt.executeUpdate("""
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
                )
            """);
            System.out.println("✓ Semesters table created");

            // Seed semesters
            System.out.println("Seeding semesters...");
            stmt.executeUpdate("""
                INSERT IGNORE INTO semesters (academic_year_id, semester_code, semester_name, start_date, end_date, is_current) VALUES
                (1, 'FALL2024', 'Fall 2024', '2024-09-01', '2024-12-20', FALSE),
                (1, 'SPRING2025', 'Spring 2025', '2025-01-15', '2025-05-15', TRUE)
            """);
            System.out.println("✓ Fall 2024 & Spring 2025 semesters seeded");

            // ================================================================
            // REGISTRATION PERIODS TABLE
            // ================================================================
            System.out.println("Creating registration_periods table...");
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS registration_periods (
                    id              INT AUTO_INCREMENT PRIMARY KEY,
                    semester_id     INT NOT NULL,
                    period_name     VARCHAR(100) NOT NULL,
                    start_date      DATETIME NOT NULL,
                    end_date        DATETIME NOT NULL,
                    is_active       BOOLEAN DEFAULT TRUE,
                    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE CASCADE
                )
            """);
            System.out.println("✓ Registration periods table created");

            // Seed registration periods
            System.out.println("Seeding registration periods...");
            stmt.executeUpdate("""
                INSERT IGNORE INTO registration_periods (semester_id, period_name, start_date, end_date, is_active) VALUES
                (2, 'Spring 2025 Registration', '2024-11-01 00:00:00', '2025-01-10 23:59:59', TRUE)
            """);
            System.out.println("✓ Registration periods seeded");

            // ================================================================
            // STUDENTS TABLE (Legacy - kept for backward compatibility)
            // ================================================================
            System.out.println("Creating students table (legacy)...");
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS students (
                    id          INT AUTO_INCREMENT PRIMARY KEY,
                    name        VARCHAR(100) NOT NULL,
                    email       VARCHAR(100) NOT NULL UNIQUE,
                    department  VARCHAR(100) NOT NULL,
                    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);
            System.out.println("✓ Students table created");

            // ================================================================
            // STUDENTS_V2 TABLE (Enhanced)
            // ================================================================
            System.out.println("Creating students_v2 table...");
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS students_v2 (
                    id              INT AUTO_INCREMENT PRIMARY KEY,
                    student_id      VARCHAR(20) NOT NULL UNIQUE,
                    name            VARCHAR(100) NOT NULL,
                    email           VARCHAR(100) NOT NULL UNIQUE,
                    department_id   INT NOT NULL,
                    year_level      INT DEFAULT 1,
                    gpa             DECIMAL(3,2) DEFAULT 0.00,
                    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE RESTRICT
                )
            """);
            System.out.println("✓ Students_v2 table created");

            // ================================================================
            // INSTRUCTORS TABLE
            // ================================================================
            System.out.println("Creating instructors table...");
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS instructors (
                    id          INT AUTO_INCREMENT PRIMARY KEY,
                    name        VARCHAR(100) NOT NULL,
                    email       VARCHAR(100) NOT NULL UNIQUE,
                    department  VARCHAR(100) NOT NULL,
                    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);
            System.out.println("✓ Instructors table created");

            // ================================================================
            // COURSES TABLE (Legacy - kept for backward compatibility)
            // ================================================================
            System.out.println("Creating courses table (legacy)...");
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS courses (
                    id              INT AUTO_INCREMENT PRIMARY KEY,
                    code            VARCHAR(20) NOT NULL UNIQUE,
                    title           VARCHAR(200) NOT NULL,
                    instructor_id   INT NULL,
                    credits         INT NOT NULL DEFAULT 3,
                    capacity        INT NOT NULL DEFAULT 30,
                    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (instructor_id) REFERENCES instructors(id) ON DELETE SET NULL
                )
            """);
            System.out.println("✓ Courses table created");

            // ================================================================
            // COURSES_V2 TABLE (Enhanced)
            // ================================================================
            System.out.println("Creating courses_v2 table...");
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS courses_v2 (
                    id              INT AUTO_INCREMENT PRIMARY KEY,
                    course_code     VARCHAR(20) NOT NULL,
                    title           VARCHAR(200) NOT NULL,
                    description     TEXT,
                    department_id   INT NOT NULL,
                    instructor_id   INT NULL,
                    credits         INT NOT NULL DEFAULT 3,
                    capacity        INT NOT NULL DEFAULT 30,
                    semester_id     INT NOT NULL,
                    year_level      INT DEFAULT 1,
                    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    UNIQUE KEY unique_course_semester (course_code, semester_id),
                    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE RESTRICT,
                    FOREIGN KEY (instructor_id) REFERENCES instructors(id) ON DELETE SET NULL,
                    FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE CASCADE
                )
            """);
            System.out.println("✓ Courses_v2 table created");

            // ================================================================
            // COURSE PREREQUISITES TABLE
            // ================================================================
            System.out.println("Creating course_prerequisites table...");
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS course_prerequisites (
                    id                  INT AUTO_INCREMENT PRIMARY KEY,
                    course_id           INT NOT NULL,
                    prerequisite_code   VARCHAR(20) NOT NULL,
                    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    UNIQUE KEY unique_prerequisite (course_id, prerequisite_code),
                    FOREIGN KEY (course_id) REFERENCES courses_v2(id) ON DELETE CASCADE
                )
            """);
            System.out.println("✓ Course prerequisites table created");

            // ================================================================
            // ENROLLMENTS TABLE (Legacy - kept for backward compatibility)
            // ================================================================
            System.out.println("Creating enrollments table (legacy)...");
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS enrollments (
                    id          INT AUTO_INCREMENT PRIMARY KEY,
                    student_id  INT NOT NULL,
                    course_id   INT NOT NULL,
                    enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    UNIQUE KEY unique_enrollment (student_id, course_id),
                    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
                    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
                )
            """);
            System.out.println("✓ Enrollments table created");

            // ================================================================
            // REGISTRATIONS TABLE (Enhanced with approval workflow)
            // ================================================================
            System.out.println("Creating registrations table...");
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS registrations (
                    id              INT AUTO_INCREMENT PRIMARY KEY,
                    student_id      INT NOT NULL,
                    course_id       INT NOT NULL,
                    semester_id     INT NOT NULL,
                    status          ENUM('PENDING', 'APPROVED', 'REJECTED', 'DROPPED') DEFAULT 'PENDING',
                    requested_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    processed_at    TIMESTAMP NULL,
                    processed_by    INT NULL,
                    notes           TEXT,
                    UNIQUE KEY unique_registration (student_id, course_id, semester_id),
                    FOREIGN KEY (student_id) REFERENCES students_v2(id) ON DELETE CASCADE,
                    FOREIGN KEY (course_id) REFERENCES courses_v2(id) ON DELETE CASCADE,
                    FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE CASCADE,
                    FOREIGN KEY (processed_by) REFERENCES users(id) ON DELETE SET NULL
                )
            """);
            System.out.println("✓ Registrations table created");

            // ================================================================
            // STUDENT COMPLETED COURSES TABLE
            // ================================================================
            System.out.println("Creating student_completed_courses table...");
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS student_completed_courses (
                    id              INT AUTO_INCREMENT PRIMARY KEY,
                    student_id      INT NOT NULL,
                    course_code     VARCHAR(20) NOT NULL,
                    semester_id     INT NOT NULL,
                    grade           VARCHAR(2),
                    completed_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    UNIQUE KEY unique_completion (student_id, course_code, semester_id),
                    FOREIGN KEY (student_id) REFERENCES students_v2(id) ON DELETE CASCADE,
                    FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE CASCADE
                )
            """);
            System.out.println("✓ Student completed courses table created");

            System.out.println();
            System.out.println("=================================================");
            System.out.println("  Database Schema Initialized Successfully!");
            System.out.println("=================================================");
            System.out.println();
            System.out.println("Tables Created:");
            System.out.println("  1. users (with default admin)");
            System.out.println("  2. departments (5 departments seeded)");
            System.out.println("  3. academic_years (2024-2025 seeded)");
            System.out.println("  4. semesters (Fall 2024 & Spring 2025 seeded)");
            System.out.println("  5. registration_periods");
            System.out.println("  6. students (legacy)");
            System.out.println("  7. students_v2 (enhanced with auto-generated IDs)");
            System.out.println("  8. instructors");
            System.out.println("  9. courses (legacy)");
            System.out.println(" 10. courses_v2 (enhanced with year, semester, prerequisites)");
            System.out.println(" 11. course_prerequisites");
            System.out.println(" 12. enrollments (legacy)");
            System.out.println(" 13. registrations (with approval workflow)");
            System.out.println(" 14. student_completed_courses");
            System.out.println();
            System.out.println("Default Admin Credentials:");
            System.out.println("  Username: admin");
            System.out.println("  Password: admin123");
            System.out.println("  Role: ADMIN");
            System.out.println();

        } catch (SQLException e) {
            System.err.println("✗ Failed to initialize database schema!");
            System.err.println("Error: " + e.getMessage());
            throw new RuntimeException("Failed to initialize database schema: " + e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  Database Initialization Utility");
        System.out.println("=================================================");
        System.out.println();
        
        try {
            initialize();
            
            System.out.println("=================================================");
            System.out.println("  Database Ready!");
            System.out.println("=================================================");
            
        } catch (Exception e) {
            System.err.println();
            System.err.println("✗ Database initialization failed!");
            System.err.println();
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
