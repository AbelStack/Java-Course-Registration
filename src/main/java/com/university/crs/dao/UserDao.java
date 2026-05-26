package com.university.crs.dao;

import com.university.crs.db.DatabaseConnection;
import com.university.crs.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for user authentication and registration.
 */
public class UserDao {

    public User login(String username, String password) throws SQLException {
        String sql = "SELECT id, username, role, student_id, approved, full_name, email, department FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int sid = rs.getInt("student_id");
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("role"),
                            rs.wasNull() ? null : sid,
                            rs.getBoolean("approved"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("department"));
                }
            }
        }
        return null;
    }

    /**
     * Login with role-based authentication
     */
    public User loginWithRole(String username, String password, String role) throws SQLException {
        String sql = "SELECT id, username, role, student_id, approved, full_name, email, department FROM users WHERE username = ? AND password = ? AND role = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int sid = rs.getInt("student_id");
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("role"),
                            rs.wasNull() ? null : sid,
                            rs.getBoolean("approved"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("department"));
                }
            }
        }
        return null;
    }

    public void createAccount(String username, String password, String role, String fullName, String email, String department) throws SQLException {
        String sql = "INSERT INTO users (username, password, role, full_name, email, department, approved) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);
            stmt.setString(4, fullName);
            stmt.setString(5, email);
            stmt.setString(6, department);
            // Students need approval, admins and department heads are auto-approved
            stmt.setBoolean(7, "ADMIN".equals(role) || "DEPARTMENT_HEAD".equals(role));
            stmt.executeUpdate();
        }
    }

    /**
     * Create account with explicit approval status and student_id link
     * Used when admin creates students - they are auto-approved
     */
    public void createAccountWithApproval(String username, String password, String role, String fullName, 
                                         String email, String department, boolean approved, Integer studentV2Id) throws SQLException {
        String sql = "INSERT INTO users (username, password, role, full_name, email, department, approved, student_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);
            stmt.setString(4, fullName);
            stmt.setString(5, email);
            stmt.setString(6, department);
            stmt.setBoolean(7, approved);
            if (studentV2Id != null) {
                stmt.setInt(8, studentV2Id);
            } else {
                stmt.setNull(8, Types.INTEGER);
            }
            stmt.executeUpdate();
        }
    }

    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    /**
     * Get all pending student accounts (not approved yet)
     */
    public List<User> getPendingStudents() throws SQLException {
        List<User> pending = new ArrayList<>();
        String sql = "SELECT id, username, role, student_id, approved, full_name, email, department FROM users WHERE role = 'STUDENT' AND approved = FALSE ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int sid = rs.getInt("student_id");
                pending.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.wasNull() ? null : sid,
                        rs.getBoolean("approved"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("department")));
            }
        }
        return pending;
    }
    
    /**
     * Approve a student account
     */
    public boolean approveStudent(int userId) throws SQLException {
        String sql = "UPDATE users SET approved = TRUE WHERE id = ? AND role = 'STUDENT'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Reject (delete) a student account
     */
    public boolean rejectStudent(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ? AND role = 'STUDENT' AND approved = FALSE";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Update user password
     */
    public boolean updatePassword(int userId, String newPassword) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newPassword);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        }
    }
}
