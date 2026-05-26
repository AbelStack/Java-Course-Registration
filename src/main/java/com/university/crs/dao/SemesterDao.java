package com.university.crs.dao;

import com.university.crs.db.DatabaseConnection;
import com.university.crs.model.Semester;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for semesters table
 */
public class SemesterDao {

    /**
     * Get all semesters
     */
    public List<Semester> getAllSemesters() throws SQLException {
        List<Semester> semesters = new ArrayList<>();
        String sql = """
            SELECT id, academic_year_id, semester_code, semester_name,
                   start_date, end_date, is_current
            FROM semesters
            ORDER BY start_date DESC
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                semesters.add(new Semester(
                    rs.getInt("id"),
                    rs.getInt("academic_year_id"),
                    rs.getString("semester_code"),
                    rs.getString("semester_name"),
                    rs.getDate("start_date").toLocalDate(),
                    rs.getDate("end_date").toLocalDate(),
                    rs.getBoolean("is_current")
                ));
            }
        }
        return semesters;
    }

    /**
     * Get current semester
     */
    public Semester getCurrentSemester() throws SQLException {
        String sql = """
            SELECT id, academic_year_id, semester_code, semester_name,
                   start_date, end_date, is_current
            FROM semesters
            WHERE is_current = TRUE
            LIMIT 1
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return new Semester(
                    rs.getInt("id"),
                    rs.getInt("academic_year_id"),
                    rs.getString("semester_code"),
                    rs.getString("semester_name"),
                    rs.getDate("start_date").toLocalDate(),
                    rs.getDate("end_date").toLocalDate(),
                    rs.getBoolean("is_current")
                );
            }
        }
        return null;
    }

    /**
     * Get semester by ID
     */
    public Semester getSemesterById(int id) throws SQLException {
        String sql = """
            SELECT id, academic_year_id, semester_code, semester_name,
                   start_date, end_date, is_current
            FROM semesters
            WHERE id = ?
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Semester(
                        rs.getInt("id"),
                        rs.getInt("academic_year_id"),
                        rs.getString("semester_code"),
                        rs.getString("semester_name"),
                        rs.getDate("start_date").toLocalDate(),
                        rs.getDate("end_date").toLocalDate(),
                        rs.getBoolean("is_current")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Add a new semester
     */
    public int addSemester(int academicYearId, String semesterCode, String semesterName,
                          LocalDate startDate, LocalDate endDate, boolean isCurrent) throws SQLException {
        String sql = """
            INSERT INTO semesters (academic_year_id, semester_code, semester_name,
                                  start_date, end_date, is_current)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, academicYearId);
            stmt.setString(2, semesterCode);
            stmt.setString(3, semesterName);
            stmt.setDate(4, Date.valueOf(startDate));
            stmt.setDate(5, Date.valueOf(endDate));
            stmt.setBoolean(6, isCurrent);
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
     * Set current semester (unsets all others)
     */
    public void setCurrentSemester(int semesterId) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            
            // Unset all current flags
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("UPDATE semesters SET is_current = FALSE");
            }
            
            // Set the specified semester as current
            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE semesters SET is_current = TRUE WHERE id = ?")) {
                stmt.setInt(1, semesterId);
                stmt.executeUpdate();
            }
            
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
}
