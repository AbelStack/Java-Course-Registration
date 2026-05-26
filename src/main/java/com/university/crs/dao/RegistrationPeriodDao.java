package com.university.crs.dao;

import com.university.crs.db.DatabaseConnection;
import com.university.crs.model.RegistrationPeriod;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for registration_periods table
 */
public class RegistrationPeriodDao {

    /**
     * Get all registration periods
     */
    public List<RegistrationPeriod> getAllRegistrationPeriods() throws SQLException {
        List<RegistrationPeriod> periods = new ArrayList<>();
        String sql = """
            SELECT rp.id, rp.semester_id, s.semester_name, rp.period_name,
                   rp.start_date, rp.end_date, rp.is_active
            FROM registration_periods rp
            JOIN semesters s ON rp.semester_id = s.id
            ORDER BY rp.start_date DESC
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                periods.add(new RegistrationPeriod(
                    rs.getInt("id"),
                    rs.getInt("semester_id"),
                    rs.getString("semester_name"),
                    rs.getString("period_name"),
                    rs.getTimestamp("start_date").toLocalDateTime(),
                    rs.getTimestamp("end_date").toLocalDateTime(),
                    rs.getBoolean("is_active")
                ));
            }
        }
        return periods;
    }

    /**
     * Add a new registration period
     */
    public int addRegistrationPeriod(int semesterId, String periodName,
                                    LocalDateTime startDate, LocalDateTime endDate,
                                    boolean isActive) throws SQLException {
        String sql = """
            INSERT INTO registration_periods (semester_id, period_name, start_date, end_date, is_active)
            VALUES (?, ?, ?, ?, ?)
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, semesterId);
            stmt.setString(2, periodName);
            stmt.setTimestamp(3, Timestamp.valueOf(startDate));
            stmt.setTimestamp(4, Timestamp.valueOf(endDate));
            stmt.setBoolean(5, isActive);
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
     * Update registration period
     */
    public boolean updateRegistrationPeriod(int id, int semesterId, String periodName,
                                           LocalDateTime startDate, LocalDateTime endDate,
                                           boolean isActive) throws SQLException {
        String sql = """
            UPDATE registration_periods
            SET semester_id = ?, period_name = ?, start_date = ?, end_date = ?, is_active = ?
            WHERE id = ?
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, semesterId);
            stmt.setString(2, periodName);
            stmt.setTimestamp(3, Timestamp.valueOf(startDate));
            stmt.setTimestamp(4, Timestamp.valueOf(endDate));
            stmt.setBoolean(5, isActive);
            stmt.setInt(6, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Delete registration period
     */
    public boolean deleteRegistrationPeriod(int id) throws SQLException {
        String sql = "DELETE FROM registration_periods WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Toggle active status
     */
    public boolean toggleActive(int id) throws SQLException {
        String sql = "UPDATE registration_periods SET is_active = NOT is_active WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Get active registration period for a semester
     */
    public RegistrationPeriod getActiveRegistrationPeriod(int semesterId) throws SQLException {
        String sql = """
            SELECT rp.id, rp.semester_id, s.semester_name, rp.period_name,
                   rp.start_date, rp.end_date, rp.is_active
            FROM registration_periods rp
            JOIN semesters s ON rp.semester_id = s.id
            WHERE rp.semester_id = ? AND rp.is_active = TRUE
            LIMIT 1
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, semesterId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new RegistrationPeriod(
                        rs.getInt("id"),
                        rs.getInt("semester_id"),
                        rs.getString("semester_name"),
                        rs.getString("period_name"),
                        rs.getTimestamp("start_date").toLocalDateTime(),
                        rs.getTimestamp("end_date").toLocalDateTime(),
                        rs.getBoolean("is_active")
                    );
                }
            }
        }
        return null;
    }
}
