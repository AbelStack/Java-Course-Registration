package com.university.crs.model;

/**
 * Represents a student in the system.
 */
public class Student {
    private int id;
    private String name;
    private String email;
    private String department;

    public Student(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.department = "";
    }

    public Student(int id, String name, String email, String department) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.department = department;
    }

    public int getId()              { return id; }
    public String getName()         { return name; }
    public String getEmail()        { return email; }
    public String getDepartment()   { return department; }

    @Override
    public String toString() {
        return String.format("  [%d] %-25s %-30s %s", id, name, email, department);
    }
}
