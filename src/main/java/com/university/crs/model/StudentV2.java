package com.university.crs.model;

/**
 * Enhanced student model for students_v2 table
 */
public class StudentV2 {
    private int id;
    private String studentId;  // e.g., "SWE-2026-001"
    private String name;
    private String email;
    private int departmentId;
    private String departmentName;
    private String departmentCode;
    private int yearLevel;
    private double gpa;

    public StudentV2(int id, String studentId, String name, String email, 
                     int departmentId, String departmentName, String departmentCode,
                     int yearLevel, double gpa) {
        this.id = id;
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.departmentCode = departmentCode;
        this.yearLevel = yearLevel;
        this.gpa = gpa;
    }

    // Getters
    public int getId() { return id; }
    public String getStudentId() { return studentId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public int getDepartmentId() { return departmentId; }
    public String getDepartmentName() { return departmentName; }
    public String getDepartmentCode() { return departmentCode; }
    public int getYearLevel() { return yearLevel; }
    public double getGpa() { return gpa; }

    @Override
    public String toString() {
        return String.format("[%s] %-25s %-30s %s Year %d GPA: %.2f", 
            studentId, name, email, departmentName, yearLevel, gpa);
    }
}
