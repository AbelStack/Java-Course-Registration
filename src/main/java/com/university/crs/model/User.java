package com.university.crs.model;

/**
 * Represents a logged-in user (ADMIN, STUDENT, or DEPARTMENT_HEAD).
 */
public class User {
    private int id;
    private String username;
    private String role;
    private Integer studentId;
    private boolean approved;
    private String fullName;
    private String email;
    private String department;

    public User(int id, String username, String role, Integer studentId, boolean approved, 
                String fullName, String email, String department) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.studentId = studentId;
        this.approved = approved;
        this.fullName = fullName;
        this.email = email;
        this.department = department;
    }

    public int getId()              { return id; }
    public String getUsername()     { return username; }
    public String getRole()         { return role; }
    public Integer getStudentId()   { return studentId; }
    public boolean isApproved()     { return approved; }
    public String getFullName()     { return fullName; }
    public String getEmail()        { return email; }
    public String getDepartment()   { return department; }
    public boolean isAdmin()        { return "ADMIN".equals(role); }
    public boolean isStudent()      { return "STUDENT".equals(role); }
    public boolean isDepartmentHead() { return "DEPARTMENT_HEAD".equals(role); }
    
    // Setters
    public void setEmail(String email) { this.email = email; }
}
