package com.university.crs.model;

/**
 * Represents a department in the system
 */
public class Department {
    private int id;
    private String code;
    private String name;

    public Department(int id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public int getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return name + " (" + code + ")";
    }
}
