package com.university.crs.dao;

import com.university.crs.db.DatabaseConnection;
import com.university.crs.model.StudentV2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for students_v2 table with enhanced features
 */
public class StudentV2Dao {

    /**
     * Add a new student to students_v2 table
     */
    public int addStudent(String studentId, String name, String email, int departmentId, int yearLevel) throws SQLException {
        String sql = "INSERT INTO students_v2 (student_id, name, email, department_id, year_level) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, studentId);
            stmt.setString(2, name);
            stmt.setString(3, email);
            stmt.setInt(4, departmentId);
            stmt.setInt(5, yearLevel);
            stmt.executeUpdate();
            
            // Get the generated ID
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    /**
     * Get all students with department information
     */
    public List<StudentV2> getAllStudents() throws SQLException {
        List<StudentV2> students = new ArrayList<>();
        String sql = """
            SELECT s.id, s.student_id, s.name, s.email, s.department_id, 
                   d.name as department_name, d.code as department_code,
                   s.year_level, s.gpa, s.created_at
            FROM students_v2 s
            JOIN departments d ON s.department_id = d.id
            ORDER BY s.student_id
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                students.add(new StudentV2(
                    rs.getInt("id"),
                    rs.getString("student_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getInt("department_id"),
                    rs.getString("department_name"),
                    rs.getString("department_code"),
                    rs.getInt("year_level"),
                    rs.getDouble("gpa")
                ));
            }
        }
        return students;
    }

    /**
     * Get student by ID
     */
    public StudentV2 getStudentById(int id) throws SQLException {
        String sql = """
            SELECT s.id, s.student_id, s.name, s.email, s.department_id, 
                   d.name as department_name, d.code as department_code,
                   s.year_level, s.gpa
            FROM students_v2 s
            JOIN departments d ON s.department_id = d.id
            WHERE s.id = ?
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new StudentV2(
                        rs.getInt("id"),
                        rs.getString("student_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getInt("department_id"),
                        rs.getString("department_name"),
                        rs.getString("department_code"),
                        rs.getInt("year_level"),
                        rs.getDouble("gpa")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Get student by student_id (e.g., "SWE-2026-001")
     */
    public StudentV2 getStudentByStudentId(String studentId) throws SQLException {
        String sql = """
            SELECT s.id, s.student_id, s.name, s.email, s.department_id, 
                   d.name as department_name, d.code as department_code,
                   s.year_level, s.gpa
            FROM students_v2 s
            JOIN departments d ON s.department_id = d.id
            WHERE s.student_id = ?
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new StudentV2(
                        rs.getInt("id"),
                        rs.getString("student_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getInt("department_id"),
                        rs.getString("department_name"),
                        rs.getString("department_code"),
                        rs.getInt("year_level"),
                        rs.getDouble("gpa")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Update student information
     */
    public boolean updateStudent(int id, String name, String email, int departmentId, int yearLevel) throws SQLException {
        String sql = "UPDATE students_v2 SET name = ?, email = ?, department_id = ?, year_level = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setInt(3, departmentId);
            stmt.setInt(4, yearLevel);
            stmt.setInt(5, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Delete student
     */
    public boolean deleteStudent(int id) throws SQLException {
        String sql = "DELETE FROM students_v2 WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Get next sequence number for student ID generation
     */
    public int getNextSequenceNumber(String departmentCode, String year) throws SQLException {
        String sql = """
            SELECT COUNT(*) + 1 as next_seq
            FROM students_v2
            WHERE student_id LIKE ?
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, departmentCode + "-" + year + "-%");
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("next_seq");
                }
            }
        }
        return 1;
    }

    /**
     * Get department ID by code
     */
    public int getDepartmentIdByCode(String code) throws SQLException {
        String sql = "SELECT id FROM departments WHERE code = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, code);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        throw new SQLException("Department not found: " + code);
    }

    /**
     * Get department ID by name
     */
    public int getDepartmentIdByName(String name) throws SQLException {
        String sql = "SELECT id FROM departments WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        throw new SQLException("Department not found: " + name);
    }
}
