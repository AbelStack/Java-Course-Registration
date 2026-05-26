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
}
