package com.university.crs.model;

import java.time.LocalDate;

/**
 * Represents a semester in the academic year
 */
public class Semester {
    private int id;
    private int academicYearId;
    private String semesterCode;
    private String semesterName;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isCurrent;

    public Semester(int id, int academicYearId, String semesterCode, String semesterName,
                   LocalDate startDate, LocalDate endDate, boolean isCurrent) {
        this.id = id;
        this.academicYearId = academicYearId;
        this.semesterCode = semesterCode;
        this.semesterName = semesterName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isCurrent = isCurrent;
    }

    // Getters
    public int getId() { return id; }
    public int getAcademicYearId() { return academicYearId; }
    public String getSemesterCode() { return semesterCode; }
    public String getSemesterName() { return semesterName; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public boolean isCurrent() { return isCurrent; }

    @Override
    public String toString() {
        return semesterName + (isCurrent ? " (Current)" : "");
    }
}
