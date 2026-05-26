package com.university.crs.dao;

import com.university.crs.db.DatabaseConnection;
import com.university.crs.model.Department;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for departments table
 */
public class DepartmentDao {

    /**
     * Add a new department
     */
    public int addDepartment(String code, String name) throws SQLException {
        String sql = "INSERT INTO departments (code, name) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, code);
            stmt.setString(2, name);
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
     * Get all departments
     */
    public List<Department> getAllDepartments() throws SQLException {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT id, code, name, created_at FROM departments ORDER BY name";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                departments.add(new Department(
                    rs.getInt("id"),
                    rs.getString("code"),
                    rs.getString("name")
                ));
            }
        }
        return departments;
    }

    /**
     * Get department by ID
     */
    public Department getDepartmentById(int id) throws SQLException {
        String sql = "SELECT id, code, name FROM departments WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Department(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("name")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Update department
     */
    public boolean updateDepartment(int id, String code, String name) throws SQLException {
        String sql = "UPDATE departments SET code = ?, name = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, code);
            stmt.setString(2, name);
            stmt.setInt(3, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Delete department
     */
    public boolean deleteDepartment(int id) throws SQLException {
        String sql = "DELETE FROM departments WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Check if department code exists
     */
    public boolean codeExists(String code) throws SQLException {
        String sql = "SELECT id FROM departments WHERE code = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, code);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
