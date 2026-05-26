package com.university.crs.gui;

import com.university.crs.dao.*;
import com.university.crs.model.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Department Head Registration Approvals Page
 * Shows only registration requests from students in the department head's department
 */
public class DeptHeadRegistrationApprovalsPage {

    private final Stage stage;
    private final User user;
    private final RegistrationDao registrationDao = new RegistrationDao();
    private final StudentV2Dao studentDao = new StudentV2Dao();
    private final CourseV2Dao courseDao = new CourseV2Dao();
    private final UserDao userDao = new UserDao();
    
    private VBox mainContainer;
    private ComboBox<String> statusFilter;

    public DeptHeadRegistrationApprovalsPage(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
    }

    public Node build() {
        mainContainer = new VBox(30);
        mainContainer.setPadding(new Insets(40, 50, 40, 50));
        mainContainer.setStyle("-fx-background-color: " + ColorScheme.BACKGROUND_HEX + ";");

        // Header
        VBox header = buildHeader();

        // Stats cards
        HBox statsCards = buildStatsCards();

        // Filters
        HBox filters = buildFilters();

        // Registrations container
        VBox registrationsContainer = new VBox(20);
        loadRegistrations(registrationsContainer, "PENDING");

        mainContainer.getChildren().addAll(header, statsCards, filters, registrationsContainer);
        
        ScrollPane scrollPane = new ScrollPane(mainContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + ColorScheme.BACKGROUND_HEX + "; -fx-background-color: " + ColorScheme.BACKGROUND_HEX + ";");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        return scrollPane;
    }

    private VBox buildHeader() {
        VBox header = new VBox(8);
        
        Label heading = new Label("Registration Approvals");
        heading.setFont(FontLoader.getPoppinsBold(28));
        heading.setTextFill(ColorScheme.DARK_TEXT);
        
        Label subtitle = new Label("Review and approve student registration requests for your department");
        subtitle.setFont(FontLoader.getOutfit(14));
        subtitle.setTextFill(ColorScheme.MEDIUM_TEXT);
        
        header.getChildren().addAll(heading, subtitle);
        return header;
    }

    private HBox buildStatsCards() {
        HBox statsRow = new HBox(20);
        statsRow.setAlignment(Pos.CENTER_LEFT);
        
        try {
            // Get department head's department
            String deptHeadDepartment = user.getDepartment();
            
            // Get all registrations for students in this department
            List<Registration> allRegistrations = registrationDao.getAllRegistrations();
            List<Registration> deptRegistrations = allRegistrations.stream()
                .filter(reg -> {
                    try {
                        StudentV2 student = studentDao.getStudentById(reg.getStudentId());
                        return student != null && student.getDepartmentName().equals(deptHeadDepartment);
                    } catch (SQLException e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
            
            long pendingCount = deptRegistrations.stream()
                .filter(r -> r.getStatus().equals("PENDING"))
                .count();
            
            long approvedCount = deptRegistrations.stream()
                .filter(r -> r.getStatus().equals("APPROVED"))
                .count();
            
            long rejectedCount = deptRegistrations.stream()
                .filter(r -> r.getStatus().equals("REJECTED"))
                .count();
            
            statsRow.getChildren().addAll(
                createStatCard("⏳", "Pending Requests", String.valueOf(pendingCount), ColorScheme.WARNING_500_HEX, ColorScheme.WARNING_50_HEX),
                createStatCard("✅", "Approved", String.valueOf(approvedCount), ColorScheme.SUCCESS_500_HEX, ColorScheme.SUCCESS_50_HEX),
                createStatCard("❌", "Rejected", String.valueOf(rejectedCount), ColorScheme.ERROR_500_HEX, ColorScheme.ERROR_50_HEX)
            );
            
        } catch (SQLException e) {
            System.err.println("Error loading registration stats: " + e.getMessage());
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
        card.setPrefWidth(200);
        card.setAlignment(Pos.TOP_LEFT);
        
        // Icon
        Label iconLabel = new Label(icon);
        iconLabel.setFont(FontLoader.getOutfitBold(32));
        iconLabel.setStyle(
            "-fx-background-color: " + bgColor + "; " +
            "-fx-padding: 12; " +
            "-fx-background-radius: 12;"
        );
        
        // Value
        Label valueLabel = new Label(value);
        valueLabel.setFont(FontLoader.getPoppinsBold(32));
        valueLabel.setTextFill(ColorScheme.DARK_TEXT);
        
        // Label
        Label textLabel = new Label(label);
        textLabel.setFont(FontLoader.getOutfit(14));
        textLabel.setTextFill(ColorScheme.MEDIUM_TEXT);
        
        card.getChildren().addAll(iconLabel, valueLabel, textLabel);
        
        return card;
    }

    private HBox buildFilters() {
        HBox filters = new HBox(15);
        filters.setAlignment(Pos.CENTER_LEFT);
        
        Label filterLabel = new Label("Filter by Status:");
        filterLabel.setFont(FontLoader.getOutfitMedium(14));
        filterLabel.setTextFill(ColorScheme.DARK_TEXT);
        
        statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("PENDING", "APPROVED", "REJECTED", "ALL");
        statusFilter.setValue("PENDING");
        statusFilter.setPrefWidth(180);
        statusFilter.setOnAction(e -> {
            VBox container = (VBox) mainContainer.getChildren().get(3);
            loadRegistrations(container, statusFilter.getValue());
        });
        
        filters.getChildren().addAll(filterLabel, statusFilter);
        
        return filters;
    }

    private void loadRegistrations(VBox container, String status) {
        container.getChildren().clear();
        
        try {
            // Get department head's department
            String deptHeadDepartment = user.getDepartment();
            
            // Get all registrations
            List<Registration> allRegistrations = registrationDao.getAllRegistrations();
            
            // Filter by department and status
            List<Registration> filteredRegistrations = allRegistrations.stream()
                .filter(reg -> {
                    try {
                        StudentV2 student = studentDao.getStudentById(reg.getStudentId());
                        if (student == null || !student.getDepartmentName().equals(deptHeadDepartment)) {
                            return false;
                        }
                        if (status.equals("ALL")) {
                            return true;
                        }
                        return reg.getStatus().equals(status);
                    } catch (SQLException e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
            
            if (filteredRegistrations.isEmpty()) {
                Label emptyLabel = new Label("No " + status.toLowerCase() + " registration requests found.");
                emptyLabel.setFont(FontLoader.getOutfit(14));
                emptyLabel.setTextFill(ColorScheme.MEDIUM_TEXT);
                emptyLabel.setPadding(new Insets(40));
                container.getChildren().add(emptyLabel);
            } else {
                for (Registration registration : filteredRegistrations) {
                    container.getChildren().add(createRegistrationCard(registration, container));
                }
            }
            
        } catch (SQLException e) {
            showAlert("Error", "Failed to load registrations: " + e.getMessage());
        }
    }

    private VBox createRegistrationCard(Registration registration, VBox parentContainer) {
        VBox card = new VBox(16);
        card.setStyle(StyleConstants.card());
        card.setPadding(new Insets(24));
        
        try {
            // Get student and course details
            StudentV2 student = studentDao.getStudentById(registration.getStudentId());
            CourseV2 course = courseDao.getCourseById(registration.getCourseId());
            
            if (student == null || course == null) {
                return card;
            }
            
            // Header row
            HBox headerRow = new HBox();
            headerRow.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(headerRow, Priority.ALWAYS);
            
            VBox infoBox = new VBox(8);
            HBox.setHgrow(infoBox, Priority.ALWAYS);
            
            // Student info
            Label studentLabel = new Label("Student: " + student.getName());
            studentLabel.setFont(FontLoader.getPoppinsBold(18));
            studentLabel.setTextFill(ColorScheme.DARK_TEXT);
            
            Label studentIdLabel = new Label("ID: " + student.getStudentId() + " | Department: " + student.getDepartmentName());
            studentIdLabel.setFont(FontLoader.getOutfit(14));
            studentIdLabel.setTextFill(ColorScheme.MEDIUM_TEXT);
            
            infoBox.getChildren().addAll(studentLabel, studentIdLabel);
            
            // Status badge
            Label statusBadge = createStatusBadge(registration.getStatus());
            
            headerRow.getChildren().addAll(infoBox, statusBadge);
            
            // Course details
            VBox courseBox = new VBox(8);
            courseBox.setStyle(
                "-fx-background-color: " + ColorScheme.GRAY_50_HEX + "; " +
                "-fx-padding: 16; " +
                "-fx-background-radius: 8;"
            );
            
            Label courseLabel = new Label("📚 " + course.getCourseCode() + " - " + course.getTitle());
            courseLabel.setFont(FontLoader.getOutfitSemiBold(16));
            courseLabel.setTextFill(ColorScheme.DARK_TEXT);
            
            HBox courseDetails = new HBox(30);
            courseDetails.setAlignment(Pos.CENTER_LEFT);
            
            Label creditsLabel = new Label("Credits: " + course.getCredits());
            creditsLabel.setFont(FontLoader.getOutfit(14));
            creditsLabel.setTextFill(ColorScheme.MEDIUM_TEXT);
            
            Label semesterLabel = new Label("Semester: " + course.getSemesterName());
            semesterLabel.setFont(FontLoader.getOutfit(14));
            semesterLabel.setTextFill(ColorScheme.MEDIUM_TEXT);
            
            Label yearLabel = new Label("Year: " + course.getYearLevel());
            yearLabel.setFont(FontLoader.getOutfit(14));
            yearLabel.setTextFill(ColorScheme.MEDIUM_TEXT);
            
            courseDetails.getChildren().addAll(creditsLabel, semesterLabel, yearLabel);
            
            courseBox.getChildren().addAll(courseLabel, courseDetails);
            
            // Request date
            Label dateLabel = new Label("Requested: " + registration.getRequestedAt().toLocalDate());
            dateLabel.setFont(FontLoader.getOutfit(13));
            dateLabel.setTextFill(ColorScheme.MEDIUM_TEXT);
            
            card.getChildren().addAll(headerRow, courseBox, dateLabel);
            
            // Action buttons (only for PENDING requests)
            if (registration.getStatus().equals("PENDING")) {
                HBox actionButtons = new HBox(15);
                actionButtons.setAlignment(Pos.CENTER_LEFT);
                
                Button approveBtn = new Button("✓ Approve");
                approveBtn.setFont(FontLoader.getOutfitSemiBold(14));
                approveBtn.setTextFill(Color.WHITE);
                approveBtn.setPrefHeight(40);
                approveBtn.setPrefWidth(140);
                approveBtn.setStyle(ColorScheme.getSuccessButtonStyle());
                approveBtn.setOnMouseEntered(e -> approveBtn.setStyle(ColorScheme.getSuccessButtonHoverStyle()));
                approveBtn.setOnMouseExited(e -> approveBtn.setStyle(ColorScheme.getSuccessButtonStyle()));
                approveBtn.setOnAction(e -> approveRegistration(registration, parentContainer));
                
                Button rejectBtn = new Button("✗ Reject");
                rejectBtn.setFont(FontLoader.getOutfitSemiBold(14));
                rejectBtn.setTextFill(Color.WHITE);
                rejectBtn.setPrefHeight(40);
                rejectBtn.setPrefWidth(140);
                rejectBtn.setStyle(ColorScheme.getDangerButtonStyle());
                rejectBtn.setOnMouseEntered(e -> rejectBtn.setStyle(ColorScheme.getDangerButtonHoverStyle()));
                rejectBtn.setOnMouseExited(e -> rejectBtn.setStyle(ColorScheme.getDangerButtonStyle()));
                rejectBtn.setOnAction(e -> rejectRegistration(registration, parentContainer));
                
                actionButtons.getChildren().addAll(approveBtn, rejectBtn);
                card.getChildren().add(actionButtons);
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading registration details: " + e.getMessage());
        }
        
        return card;
    }

    private Label createStatusBadge(String status) {
        Label badge = new Label(status);
        badge.setFont(FontLoader.getOutfitSemiBold(12));
        badge.setStyle("-fx-padding: 6 16; -fx-background-radius: 12;");
        
        switch (status) {
            case "PENDING":
                badge.setTextFill(ColorScheme.WARNING_700);
                badge.setStyle(badge.getStyle() + "-fx-background-color: " + ColorScheme.WARNING_50_HEX + ";");
                break;
            case "APPROVED":
                badge.setTextFill(ColorScheme.SUCCESS_700);
                badge.setStyle(badge.getStyle() + "-fx-background-color: " + ColorScheme.SUCCESS_50_HEX + ";");
                break;
            case "REJECTED":
                badge.setTextFill(ColorScheme.ERROR_700);
                badge.setStyle(badge.getStyle() + "-fx-background-color: " + ColorScheme.ERROR_50_HEX + ";");
                break;
        }
        
        return badge;
    }

    private void approveRegistration(Registration registration, VBox parentContainer) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Approve Registration");
        confirm.setHeaderText("Approve this registration request?");
        confirm.setContentText("The student will be enrolled in this course.");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    registrationDao.updateRegistrationStatus(registration.getId(), "APPROVED", user.getId(), null);
                    
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Success");
                    success.setHeaderText("Registration Approved");
                    success.setContentText("The student has been successfully enrolled in the course.");
                    success.showAndWait();
                    
                    // Refresh the list
                    loadRegistrations(parentContainer, statusFilter.getValue());
                    
                    // Refresh stats
                    HBox statsCards = buildStatsCards();
                    mainContainer.getChildren().set(1, statsCards);
                    
                } catch (SQLException e) {
                    showAlert("Error", "Failed to approve registration: " + e.getMessage());
                }
            }
        });
    }

    private void rejectRegistration(Registration registration, VBox parentContainer) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reject Registration");
        dialog.setHeaderText("Reject this registration request?");
        dialog.setContentText("Reason for rejection (optional):");
        
        dialog.showAndWait().ifPresent(reason -> {
            try {
                registrationDao.updateRegistrationStatus(registration.getId(), "REJECTED", user.getId(), 
                    reason.isEmpty() ? null : reason);
                
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Success");
                success.setHeaderText("Registration Rejected");
                success.setContentText("The registration request has been rejected.");
                success.showAndWait();
                
                // Refresh the list
                loadRegistrations(parentContainer, statusFilter.getValue());
                
                // Refresh stats
                HBox statsCards = buildStatsCards();
                mainContainer.getChildren().set(1, statsCards);
                
            } catch (SQLException e) {
                showAlert("Error", "Failed to reject registration: " + e.getMessage());
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
