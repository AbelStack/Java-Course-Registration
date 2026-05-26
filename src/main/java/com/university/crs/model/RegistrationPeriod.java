package com.university.crs.model;

import java.time.LocalDateTime;

/**
 * Represents a registration period for a semester
 */
public class RegistrationPeriod {
    private int id;
    private int semesterId;
    private String semesterName;
    private String periodName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isActive;

    public RegistrationPeriod(int id, int semesterId, String semesterName, String periodName,
                             LocalDateTime startDate, LocalDateTime endDate, boolean isActive) {
        this.id = id;
        this.semesterId = semesterId;
        this.semesterName = semesterName;
        this.periodName = periodName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = isActive;
    }

    // Getters
    public int getId() { return id; }
    public int getSemesterId() { return semesterId; }
    public String getSemesterName() { return semesterName; }
    public String getPeriodName() { return periodName; }
    public LocalDateTime getStartDate() { return startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public boolean isActive() { return isActive; }

    public String getStatus() {
        LocalDateTime now = LocalDateTime.now();
        if (!isActive) return "Inactive";
        if (now.isBefore(startDate)) return "Upcoming";
        if (now.isAfter(endDate)) return "Ended";
        return "Active";
    }

    @Override
    public String toString() {
        return periodName + " - " + semesterName + " (" + getStatus() + ")";
    }
}
