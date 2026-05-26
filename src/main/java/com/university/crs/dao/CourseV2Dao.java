package com.university.crs.dao;

import com.university.crs.db.DatabaseConnection;
import com.university.crs.model.CourseV2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for courses_v2 table with enhanced features
 */
public class CourseV2Dao {

    /**
     * Add a new course
     */
    public int addCourse(String courseCode, String title, String description,
                        int departmentId, Integer instructorId, int credits,
                        int capacity, int semesterId, int yearLevel) throws SQLException {
        String sql = """
            INSERT INTO courses_v2 (course_code, title, description, department_id, 
                                   instructor_id, credits, capacity, semester_id, year_level)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, courseCode);
            stmt.setString(2, title);
            stmt.setString(3, description);
            stmt.setInt(4, departmentId);
            if (instructorId != null) {
                stmt.setInt(5, instructorId);
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            stmt.setInt(6, credits);
            stmt.setInt(7, capacity);
            stmt.setInt(8, semesterId);
            stmt.setInt(9, yearLevel);
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    /**
     * Get all courses with department, instructor, and semester information
     */
    public List<CourseV2> getAllCourses() throws SQLException {
        List<CourseV2> courses = new ArrayList<>();
        String sql = """
            SELECT c.id, c.course_code, c.title, c.description,
                   c.department_id, d.name as department_name,
                   c.instructor_id, i.name as instructor_name,
                   c.credits, c.capacity,
                   c.semester_id, s.semester_name,
                   c.year_level
            FROM courses_v2 c
            JOIN departments d ON c.department_id = d.id
            LEFT JOIN instructors i ON c.instructor_id = i.id
            JOIN semesters s ON c.semester_id = s.id
            ORDER BY c.course_code
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Integer instructorId = rs.getInt("instructor_id");
                if (rs.wasNull()) instructorId = null;
                
                courses.add(new CourseV2(
                    rs.getInt("id"),
                    rs.getString("course_code"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getInt("department_id"),
                    rs.getString("department_name"),
                    instructorId,
                    rs.getString("instructor_name"),
                    rs.getInt("credits"),
                    rs.getInt("capacity"),
                    rs.getInt("semester_id"),
                    rs.getString("semester_name"),
                    rs.getInt("year_level")
                ));
            }
        }
        return courses;
    }

    /**
     * Get course by ID
     */
    public CourseV2 getCourseById(int id) throws SQLException {
        String sql = """
            SELECT c.id, c.course_code, c.title, c.description,
                   c.department_id, d.name as department_name,
                   c.instructor_id, i.name as instructor_name,
                   c.credits, c.capacity,
                   c.semester_id, s.semester_name,
                   c.year_level
            FROM courses_v2 c
            JOIN departments d ON c.department_id = d.id
            LEFT JOIN instructors i ON c.instructor_id = i.id
            JOIN semesters s ON c.semester_id = s.id
            WHERE c.id = ?
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Integer instructorId = rs.getInt("instructor_id");
                    if (rs.wasNull()) instructorId = null;
                    
                    return new CourseV2(
                        rs.getInt("id"),
                        rs.getString("course_code"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("department_id"),
                        rs.getString("department_name"),
                        instructorId,
                        rs.getString("instructor_name"),
                        rs.getInt("credits"),
                        rs.getInt("capacity"),
                        rs.getInt("semester_id"),
                        rs.getString("semester_name"),
                        rs.getInt("year_level")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Update course
     */
    public boolean updateCourse(int id, String courseCode, String title, String description,
                               int departmentId, Integer instructorId, int credits,
                               int capacity, int semesterId, int yearLevel) throws SQLException {
        String sql = """
            UPDATE courses_v2 
            SET course_code = ?, title = ?, description = ?, department_id = ?,
                instructor_id = ?, credits = ?, capacity = ?, semester_id = ?, year_level = ?
            WHERE id = ?
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, courseCode);
            stmt.setString(2, title);
            stmt.setString(3, description);
            stmt.setInt(4, departmentId);
            if (instructorId != null) {
                stmt.setInt(5, instructorId);
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            stmt.setInt(6, credits);
            stmt.setInt(7, capacity);
            stmt.setInt(8, semesterId);
            stmt.setInt(9, yearLevel);
            stmt.setInt(10, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Delete course
     */
    public boolean deleteCourse(int id) throws SQLException {
        String sql = "DELETE FROM courses_v2 WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Check if course code exists for a specific semester
     */
    public boolean courseExistsInSemester(String courseCode, int semesterId) throws SQLException {
        String sql = "SELECT id FROM courses_v2 WHERE course_code = ? AND semester_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, courseCode);
            stmt.setInt(2, semesterId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
