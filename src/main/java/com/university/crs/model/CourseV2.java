package com.university.crs.model;

/**
 * Enhanced course model for courses_v2 table
 */
public class CourseV2 {
    private int id;
    private String courseCode;
    private String title;
    private String description;
    private int departmentId;
    private String departmentName;
    private Integer instructorId;
    private String instructorName;
    private int credits;
    private int capacity;
    private int semesterId;
    private String semesterName;
    private int yearLevel;

    public CourseV2(int id, String courseCode, String title, String description,
                    int departmentId, String departmentName,
                    Integer instructorId, String instructorName,
                    int credits, int capacity,
                    int semesterId, String semesterName,
                    int yearLevel) {
        this.id = id;
        this.courseCode = courseCode;
        this.title = title;
        this.description = description;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.instructorId = instructorId;
        this.instructorName = instructorName;
        this.credits = credits;
        this.capacity = capacity;
        this.semesterId = semesterId;
        this.semesterName = semesterName;
        this.yearLevel = yearLevel;
    }

    // Getters
    public int getId() { return id; }
    public String getCourseCode() { return courseCode; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getDepartmentId() { return departmentId; }
    public String getDepartmentName() { return departmentName; }
    public Integer getInstructorId() { return instructorId; }
    public String getInstructorName() { return instructorName; }
    public int getCredits() { return credits; }
    public int getCapacity() { return capacity; }
    public int getSemesterId() { return semesterId; }
    public String getSemesterName() { return semesterName; }
    public int getYearLevel() { return yearLevel; }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s (%d credits)", 
            courseCode, title, semesterName, credits);
    }
}
