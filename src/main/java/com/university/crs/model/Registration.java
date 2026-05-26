package com.university.crs.model;

import java.time.LocalDateTime;

/**
 * Represents a course registration with approval workflow
 */
public class Registration {
    private int id;
    private int studentId;
    private String studentName;
    private String studentIdCode;
    private int courseId;
    private String courseCode;
    private String courseTitle;
    private int semesterId;
    private String semesterName;
    private String status; // PENDING, APPROVED, REJECTED, DROPPED
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
    private Integer processedBy;
    private String processedByName;
    private String notes;

    public Registration(int id, int studentId, String studentName, String studentIdCode,
                       int courseId, String courseCode, String courseTitle,
                       int semesterId, String semesterName,
                       String status, LocalDateTime requestedAt,
                       LocalDateTime processedAt, Integer processedBy, String processedByName,
                       String notes) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentIdCode = studentIdCode;
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.semesterId = semesterId;
        this.semesterName = semesterName;
        this.status = status;
        this.requestedAt = requestedAt;
        this.processedAt = processedAt;
        this.processedBy = processedBy;
        this.processedByName = processedByName;
        this.notes = notes;
    }

    // Getters
    public int getId() { return id; }
    public int getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getStudentIdCode() { return studentIdCode; }
    public int getCourseId() { return courseId; }
    public String getCourseCode() { return courseCode; }
    public String getCourseTitle() { return courseTitle; }
    public int getSemesterId() { return semesterId; }
    public String getSemesterName() { return semesterName; }
    public String getStatus() { return status; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public LocalDateTime getProcessedAt() { return processedAt; }
    public Integer getProcessedBy() { return processedBy; }
    public String getProcessedByName() { return processedByName; }
    public String getNotes() { return notes; }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s (%s) - Status: %s",
            studentIdCode, studentName, courseCode, semesterName, status);
    }
}
