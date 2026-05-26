package com.university.crs.gui;

import com.university.crs.dao.*;
import com.university.crs.model.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Department Head Overview Page
 * Shows department-specific statistics and quick actions
 */
public class DeptHeadOverviewPage {

    private final User user;
    private final StudentV2Dao studentDao = new StudentV2Dao();
    private final CourseV2Dao courseDao = new CourseV2Dao();
    private final RegistrationDao registrationDao = new RegistrationDao();
    private final DepartmentDao departmentDao = new DepartmentDao();

    public DeptHeadOverviewPage(User user) {
        this.user = user;
    }

    public Node build() {
        VBox page = new VBox(30);
        page.setPadding(new Insets(40, 50, 40, 50));
        page.setStyle("-fx-background-color: " + ColorScheme.BACKGROUND_HEX + ";");

        // Welcome header
        VBox header = buildHeader();

        // Stats cards
        HBox statsCards = buildStatsCards();

        // Two-column layout
        HBox contentRow = new HBox(20);
        HBox.setHgrow(contentRow, Priority.ALWAYS);
        
        // Left column - Pending Approvals
        VBox leftColumn = buildPendingApprovalsSection();
        HBox.setHgrow(leftColumn, Priority.ALWAYS);
        
        // Right column - Department Info
        VBox rightColumn = buildDepartmentInfoSection();
        rightColumn.setPrefWidth(400);
        
        contentRow.getChildren().addAll(leftColumn, rightColumn);

        page.getChildren().addAll(header, statsCards, contentRow);
        
        ScrollPane scrollPane = new ScrollPane(page);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + ColorScheme.BACKGROUND_HEX + "; -fx-background-color: " + ColorScheme.BACKGROUND_HEX + ";");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        return scrollPane;
    }

    private VBox buildHeader() {
        VBox header = new VBox(8);
        
        Label greeting = new Label("Welcome back, " + user.getFullName() + "!");
        greeting.setFont(FontLoader.getPoppinsBold(32));
        greeting.setTextFill(ColorScheme.DARK_TEXT);
        
        Label subtitle = new Label("Department Head - " + user.getDepartment());
        subtitle.setFont(FontLoader.getOutfit(16));
        subtitle.setTextFill(ColorScheme.MEDIUM_TEXT);
        
        header.getChildren().addAll(greeting, subtitle);
        return header;
    }

    private HBox buildStatsCards() {
        HBox statsRow = new HBox(20);
        statsRow.setAlignment(Pos.CENTER_LEFT);
        
        try {
            String deptHeadDepartment = user.getDepartment();
            
            // Get department students
            List<StudentV2> allStudents = studentDao.getAllStudents();
            List<StudentV2> deptStudents = allStudents.stream()
                .filter(s -> s.getDepartmentName().equals(deptHeadDepartment))
                .collect(Collectors.toList());
            
            // Get department courses
            List<CourseV2> allCourses = courseDao.getAllCourses();
            List<CourseV2> deptCourses = allCourses.stream()
                .filter(c -> c.getDepartmentName().equals(deptHeadDepartment))
                .collect(Collectors.toList());
            
            // Get pending registrations
            List<Registration> allRegistrations = registrationDao.getAllRegistrations();
            long pendingCount = allRegistrations.stream()
                .filter(reg -> {
                    try {
                        StudentV2 student = studentDao.getStudentById(reg.getStudentId());
                        return student != null && 
                               student.getDepartmentName().equals(deptHeadDepartment) &&
                               reg.getStatus().equals("PENDING");
                    } catch (SQLException e) {
                        return false;
                    }
                })
                .count();
            
            // Get total approved registrations
            long approvedCount = allRegistrations.stream()
                .filter(reg -> {
                    try {
                        StudentV2 student = studentDao.getStudentById(reg.getStudentId());
                        return student != null && 
                               student.getDepartmentName().equals(deptHeadDepartment) &&
                               reg.getStatus().equals("APPROVED");
                    } catch (SQLException e) {
                        return false;
                    }
                })
                .count();
            
            statsRow.getChildren().addAll(
                createStatCard("👥", "Total Students", String.valueOf(deptStudents.size()), ColorScheme.PRIMARY_600_HEX, ColorScheme.PRIMARY_50_HEX),
                createStatCard("📚", "Active Courses", String.valueOf(deptCourses.size()), ColorScheme.SUCCESS_600_HEX, ColorScheme.SUCCESS_50_HEX),
                createStatCard("⏳", "Pending Approvals", String.valueOf(pendingCount), ColorScheme.WARNING_600_HEX, ColorScheme.WARNING_50_HEX),
                createStatCard("✅", "Approved Registrations", String.valueOf(approvedCount), ColorScheme.BRAND_600_HEX, ColorScheme.BRAND_50_HEX)
            );
            
        } catch (SQLException e) {
            System.err.println("Error loading stats: " + e.getMessage());
        }
        
        return statsRow;
    }

    private VBox createStatCard(String icon, String label, String value, String iconColor, String bgColor) {
        VBox card = new VBox(12);
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 12; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 15, 0, 0, 3); " +
            "-fx-padding: 24;"
        );
        card.setPrefWidth(240);
        card.setAlignment(Pos.TOP_LEFT);
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(FontLoader.getOutfitBold(32));
        iconLabel.setStyle(
            "-fx-background-color: " + bgColor + "; " +
            "-fx-padding: 12; " +
            "-fx-background-radius: 12;"
        );
        
        Label valueLabel = new Label(value);
        valueLabel.setFont(FontLoader.getPoppinsBold(32));
        valueLabel.setTextFill(ColorScheme.DARK_TEXT);
        
        Label textLabel = new Label(label);
        textLabel.setFont(FontLoader.getOutfit(14));
        textLabel.setTextFill(ColorScheme.MEDIUM_TEXT);
        
        card.getChildren().addAll(iconLabel, valueLabel, textLabel);
        
        return card;
    }

    private VBox buildPendingApprovalsSection() {
        VBox section = new VBox(20);
        section.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 12; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 15, 0, 0, 3); " +
            "-fx-padding: 24;"
        );
        
        Label heading = new Label("Pending Registration Approvals");
        heading.setFont(FontLoader.getPoppinsBold(20));
        heading.setTextFill(ColorScheme.DARK_TEXT);
        
        VBox approvalsList = new VBox(12);
        
        try {
            String deptHeadDepartment = user.getDepartment();
            List<Registration> allRegistrations = registrationDao.getAllRegistrations();
            
            List<Registration> pendingRegistrations = allRegistrations.stream()
                .filter(reg -> {
                    try {
                        StudentV2 student = studentDao.getStudentById(reg.getStudentId());
                        return student != null && 
                               student.getDepartmentName().equals(deptHeadDepartment) &&
                               reg.getStatus().equals("PENDING");
                    } catch (SQLException e) {
                        return false;
                    }
                })
                .limit(5)
                .collect(Collectors.toList());
            
            if (pendingRegistrations.isEmpty()) {
                Label emptyLabel = new Label("No pending approvals at the moment.");
                emptyLabel.setFont(FontLoader.getOutfit(14));
                emptyLabel.setTextFill(ColorScheme.MEDIUM_TEXT);
                emptyLabel.setPadding(new Insets(20));
                approvalsList.getChildren().add(emptyLabel);
            } else {
                for (Registration reg : pendingRegistrations) {
                    approvalsList.getChildren().add(createApprovalItem(reg));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading pending approvals: " + e.getMessage());
        }
        
        section.getChildren().addAll(heading, approvalsList);
        
        return section;
    }

    private HBox createApprovalItem(Registration registration) {
        HBox item = new HBox(15);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(12));
        item.setStyle(
            "-fx-background-color: " + ColorScheme.GRAY_50_HEX + "; " +
            "-fx-background-radius: 8;"
        );
        
        try {
            StudentV2 student = studentDao.getStudentById(registration.getStudentId());
            CourseV2 course = courseDao.getCourseById(registration.getCourseId());
            
            if (student != null && course != null) {
                VBox infoBox = new VBox(4);
                HBox.setHgrow(infoBox, Priority.ALWAYS);
                
                Label studentLabel = new Label(student.getName() + " (" + student.getStudentId() + ")");
                studentLabel.setFont(FontLoader.getOutfitSemiBold(14));
                studentLabel.setTextFill(ColorScheme.DARK_TEXT);
                
                Label courseLabel = new Label(course.getCourseCode() + " - " + course.getTitle());
                courseLabel.setFont(FontLoader.getOutfit(13));
                courseLabel.setTextFill(ColorScheme.MEDIUM_TEXT);
                
                infoBox.getChildren().addAll(studentLabel, courseLabel);
                
                Label statusBadge = new Label("PENDING");
                statusBadge.setFont(FontLoader.getOutfitSemiBold(11));
                statusBadge.setTextFill(ColorScheme.WARNING_700);
                statusBadge.setStyle(
                    "-fx-background-color: " + ColorScheme.WARNING_50_HEX + "; " +
                    "-fx-padding: 4 10; " +
                    "-fx-background-radius: 10;"
                );
                
                item.getChildren().addAll(infoBox, statusBadge);
            }
        } catch (SQLException e) {
            System.err.println("Error loading approval item: " + e.getMessage());
        }
        
        return item;
    }

    private VBox buildDepartmentInfoSection() {
        VBox section = new VBox(20);
        section.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 12; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 15, 0, 0, 3); " +
            "-fx-padding: 24;"
        );
        
        Label heading = new Label("Department Information");
        heading.setFont(FontLoader.getPoppinsBold(20));
        heading.setTextFill(ColorScheme.DARK_TEXT);
        
        VBox infoList = new VBox(16);
        
        try {
            String deptHeadDepartment = user.getDepartment();
            
            // Get department details
            List<Department> departments = departmentDao.getAllDepartments();
            Department dept = departments.stream()
                .filter(d -> d.getName().equals(deptHeadDepartment))
                .findFirst()
                .orElse(null);
            
            if (dept != null) {
                infoList.getChildren().addAll(
                    createInfoItem("🏢", "Department", dept.getName()),
                    createInfoItem("🔖", "Code", dept.getCode()),
                    createInfoItem("👤", "Department Head", user.getFullName()),
                    createInfoItem("📧", "Email", user.getEmail())
                );
            }
            
            // Quick actions
            VBox actionsBox = new VBox(12);
            actionsBox.setPadding(new Insets(16, 0, 0, 0));
            
            Label actionsLabel = new Label("Quick Actions");
            actionsLabel.setFont(FontLoader.getOutfitSemiBold(14));
            actionsLabel.setTextFill(ColorScheme.DARK_TEXT);
            
            Button viewApprovalsBtn = new Button("View All Approvals →");
            viewApprovalsBtn.setFont(FontLoader.getOutfitMedium(13));
            viewApprovalsBtn.setTextFill(ColorScheme.PRIMARY_600);
            viewApprovalsBtn.setStyle(
                "-fx-background-color: " + ColorScheme.PRIMARY_50_HEX + "; " +
                "-fx-padding: 10 16; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;"
            );
            viewApprovalsBtn.setMaxWidth(Double.MAX_VALUE);
            
            Button viewStudentsBtn = new Button("View All Students →");
            viewStudentsBtn.setFont(FontLoader.getOutfitMedium(13));
            viewStudentsBtn.setTextFill(ColorScheme.SUCCESS_600);
            viewStudentsBtn.setStyle(
                "-fx-background-color: " + ColorScheme.SUCCESS_50_HEX + "; " +
                "-fx-padding: 10 16; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;"
            );
            viewStudentsBtn.setMaxWidth(Double.MAX_VALUE);
            
            actionsBox.getChildren().addAll(actionsLabel, viewApprovalsBtn, viewStudentsBtn);
            
            infoList.getChildren().add(actionsBox);
            
        } catch (SQLException e) {
            System.err.println("Error loading department info: " + e.getMessage());
        }
        
        section.getChildren().addAll(heading, infoList);
        
        return section;
    }

    private HBox createInfoItem(String icon, String label, String value) {
        HBox item = new HBox(12);
        item.setAlignment(Pos.CENTER_LEFT);
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(FontLoader.getOutfitBold(20));
        
        VBox textBox = new VBox(2);
        HBox.setHgrow(textBox, Priority.ALWAYS);
        
        Label labelText = new Label(label);
        labelText.setFont(FontLoader.getOutfit(12));
        labelText.setTextFill(ColorScheme.MEDIUM_TEXT);
        
        Label valueText = new Label(value);
        valueText.setFont(FontLoader.getOutfitSemiBold(14));
        valueText.setTextFill(ColorScheme.DARK_TEXT);
        
        textBox.getChildren().addAll(labelText, valueText);
        
        item.getChildren().addAll(iconLabel, textBox);
        
        return item;
    }
}
