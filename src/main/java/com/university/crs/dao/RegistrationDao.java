package com.university.crs.dao;

import com.university.crs.db.DatabaseConnection;
import com.university.crs.model.Registration;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for registrations table (enhanced enrollments with approval)
 */
public class RegistrationDao {

    /**
     * Get all registrations
     */
    public List<Registration> getAllRegistrations() throws SQLException {
        List<Registration> registrations = new ArrayList<>();
        String sql = """
            SELECT r.id, r.student_id, st.name as student_name, st.student_id as student_id_code,
                   r.course_id, c.course_code, c.title as course_title,
                   r.semester_id, s.semester_name,
                   r.status, r.requested_at, r.processed_at,
                   r.processed_by, u.full_name as processed_by_name, r.notes
            FROM registrations r
            JOIN students_v2 st ON r.student_id = st.id
            JOIN courses_v2 c ON r.course_id = c.id
            JOIN semesters s ON r.semester_id = s.id
            LEFT JOIN users u ON r.processed_by = u.id
            ORDER BY r.requested_at DESC
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Integer processedBy = rs.getInt("processed_by");
                if (rs.wasNull()) processedBy = null;
                
                Timestamp processedAtTs = rs.getTimestamp("processed_at");
                LocalDateTime processedAt = processedAtTs != null ? processedAtTs.toLocalDateTime() : null;
                
                registrations.add(new Registration(
                    rs.getInt("id"),
                    rs.getInt("student_id"),
                    rs.getString("student_name"),
                    rs.getString("student_id_code"),
                    rs.getInt("course_id"),
                    rs.getString("course_code"),
                    rs.getString("course_title"),
                    rs.getInt("semester_id"),
                    rs.getString("semester_name"),
                    rs.getString("status"),
                    rs.getTimestamp("requested_at").toLocalDateTime(),
                    processedAt,
                    processedBy,
                    rs.getString("processed_by_name"),
                    rs.getString("notes")
                ));
            }
        }
        return registrations;
    }

    /**
     * Get pending registrations
     */
    public List<Registration> getPendingRegistrations() throws SQLException {
        List<Registration> registrations = new ArrayList<>();
        String sql = """
            SELECT r.id, r.student_id, st.name as student_name, st.student_id as student_id_code,
                   r.course_id, c.course_code, c.title as course_title,
                   r.semester_id, s.semester_name,
                   r.status, r.requested_at, r.processed_at,
                   r.processed_by, u.full_name as processed_by_name, r.notes
            FROM registrations r
            JOIN students_v2 st ON r.student_id = st.id
            JOIN courses_v2 c ON r.course_id = c.id
            JOIN semesters s ON r.semester_id = s.id
            LEFT JOIN users u ON r.processed_by = u.id
            WHERE r.status = 'PENDING'
            ORDER BY r.requested_at ASC
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Integer processedBy = rs.getInt("processed_by");
                if (rs.wasNull()) processedBy = null;
                
                Timestamp processedAtTs = rs.getTimestamp("processed_at");
                LocalDateTime processedAt = processedAtTs != null ? processedAtTs.toLocalDateTime() : null;
                
                registrations.add(new Registration(
                    rs.getInt("id"),
                    rs.getInt("student_id"),
                    rs.getString("student_name"),
                    rs.getString("student_id_code"),
                    rs.getInt("course_id"),
                    rs.getString("course_code"),
                    rs.getString("course_title"),
                    rs.getInt("semester_id"),
                    rs.getString("semester_name"),
                    rs.getString("status"),
                    rs.getTimestamp("requested_at").toLocalDateTime(),
                    processedAt,
                    processedBy,
                    rs.getString("processed_by_name"),
                    rs.getString("notes")
                ));
            }
        }
        return registrations;
    }

    /**
     * Update registration status (generic method for approve/reject)
     */
    public boolean updateRegistrationStatus(int registrationId, String status, int processedByUserId, String notes) throws SQLException {
        String sql = """
            UPDATE registrations
            SET status = ?, processed_at = NOW(), processed_by = ?, notes = ?
            WHERE id = ?
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, processedByUserId);
            stmt.setString(3, notes);
            stmt.setInt(4, registrationId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Approve a registration
     */
    public boolean approveRegistration(int registrationId, int processedByUserId, String notes) throws SQLException {
        String sql = """
            UPDATE registrations
            SET status = 'APPROVED', processed_at = NOW(), processed_by = ?, notes = ?
            WHERE id = ? AND status = 'PENDING'
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, processedByUserId);
            stmt.setString(2, notes);
            stmt.setInt(3, registrationId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Reject a registration
     */
    public boolean rejectRegistration(int registrationId, int processedByUserId, String notes) throws SQLException {
        String sql = """
            UPDATE registrations
            SET status = 'REJECTED', processed_at = NOW(), processed_by = ?, notes = ?
            WHERE id = ? AND status = 'PENDING'
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, processedByUserId);
            stmt.setString(2, notes);
            stmt.setInt(3, registrationId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Drop a registration
     */
    public boolean dropRegistration(int registrationId) throws SQLException {
        String sql = "UPDATE registrations SET status = 'DROPPED' WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, registrationId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Delete registration
     */
    public boolean deleteRegistration(int id) throws SQLException {
        String sql = "DELETE FROM registrations WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Get registration count by status
     */
    public int getCountByStatus(String status) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM registrations WHERE status = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        }
        return 0;
    }

    /**
     * Get registrations by student
     */
    public List<Registration> getRegistrationsByStudent(int studentId) throws SQLException {
        List<Registration> registrations = new ArrayList<>();
        String sql = """
            SELECT r.id, r.student_id, st.name as student_name, st.student_id as student_id_code,
                   r.course_id, c.course_code, c.title as course_title, c.credits,
                   r.semester_id, s.semester_name,
                   r.status, r.requested_at, r.processed_at,
                   r.processed_by, u.full_name as processed_by_name, r.notes
            FROM registrations r
            JOIN students_v2 st ON r.student_id = st.id
            JOIN courses_v2 c ON r.course_id = c.id
            JOIN semesters s ON r.semester_id = s.id
            LEFT JOIN users u ON r.processed_by = u.id
            WHERE r.student_id = ?
            ORDER BY r.requested_at DESC
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Integer processedBy = rs.getInt("processed_by");
                    if (rs.wasNull()) processedBy = null;
                    
                    Timestamp processedAtTs = rs.getTimestamp("processed_at");
                    LocalDateTime processedAt = processedAtTs != null ? processedAtTs.toLocalDateTime() : null;
                    
                    Registration reg = new Registration(
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        rs.getString("student_name"),
                        rs.getString("student_id_code"),
                        rs.getInt("course_id"),
                        rs.getString("course_code"),
                        rs.getString("course_title"),
                        rs.getInt("semester_id"),
                        rs.getString("semester_name"),
                        rs.getString("status"),
                        rs.getTimestamp("requested_at").toLocalDateTime(),
                        processedAt,
                        processedBy,
                        rs.getString("processed_by_name"),
                        rs.getString("notes")
                    );
                    reg.setCredits(rs.getInt("credits"));
                    registrations.add(reg);
                }
            }
        }
        return registrations;
    }

    /**
     * Get pending registrations count for a student
     */
    public int getPendingRegistrationsCount(int studentId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM registrations WHERE student_id = ? AND status = 'PENDING'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        }
        return 0;
    }

    /**
     * Get approved registrations count for a student
     */
    public int getApprovedRegistrationsCount(int studentId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM registrations WHERE student_id = ? AND status = 'APPROVED'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        }
        return 0;
    }

    /**
     * Get rejected registrations count for a student
     */
    public int getRejectedRegistrationsCount(int studentId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM registrations WHERE student_id = ? AND status = 'REJECTED'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        }
        return 0;
    }

    /**
     * Get total credits for a student (approved courses)
     */
    public int getTotalCreditsForStudent(int studentId) throws SQLException {
        String sql = """
            SELECT COALESCE(SUM(c.credits), 0) as total_credits
            FROM registrations r
            JOIN courses_v2 c ON r.course_id = c.id
            WHERE r.student_id = ? AND r.status = 'APPROVED'
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total_credits");
                }
            }
        }
        return 0;
    }

    /**
     * Check if student is already registered for a course
     */
    public boolean isStudentRegisteredForCourse(int studentId, int courseId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM registrations WHERE student_id = ? AND course_id = ? AND status IN ('PENDING', 'APPROVED')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        }
        return false;
    }

    /**
     * Create a new registration request
     */
    public int createRegistration(int studentId, int courseId, int semesterId) throws SQLException {
        String sql = """
            INSERT INTO registrations (student_id, course_id, semester_id, status, requested_at)
            VALUES (?, ?, ?, 'PENDING', NOW())
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            stmt.setInt(3, semesterId);
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }
}
