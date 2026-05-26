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

/**
 * Student Courses Page - Browse and register for available courses
 * Implements the course registration workflow with validation
 */
public class StudentCoursesPage {

    private final Stage stage;
    private final User user;
    private final CourseDao courseDao = new CourseDao();
    private final StudentV2Dao studentDao = new StudentV2Dao();
    private final RegistrationDao registrationDao = new RegistrationDao();
    private final RegistrationPeriodDao periodDao = new RegistrationPeriodDao();
    private final SemesterDao semesterDao = new SemesterDao();
    
    private VBox coursesContainer;
    private StudentV2 currentStudent;
    private ComboBox<String> semesterFilter;
    private ComboBox<String> departmentFilter;

    public StudentCoursesPage(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
    }

    public Node build() {
        VBox page = new VBox(30);
        page.setPadding(new Insets(40, 50, 40, 50));
        page.setStyle("-fx-background-color: " + ColorScheme.BACKGROUND_HEX + ";");

        // Load current student
        try {
            currentStudent = studentDao.getStudentByStudentId(user.getUsername());
        } catch (SQLException e) {
            showAlert("Error", "Failed to load student information: " + e.getMessage());
            return page;
        }

        // Header
        VBox header = buildHeader();

        // Registration status banner
        HBox statusBanner = buildRegistrationStatusBanner();

        // Filters
        HBox filters = buildFilters();

        // Courses grid
        coursesContainer = new VBox(20);
        refreshCourses();

        page.getChildren().addAll(header, statusBanner, filters, coursesContainer);
        
        ScrollPane scrollPane = new ScrollPane(page);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + ColorScheme.BACKGROUND_HEX + "; -fx-background-color: " + ColorScheme.BACKGROUND_HEX + ";");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        return scrollPane;
    }

    private VBox buildHeader() {
        VBox header = new VBox(8);
        
        Label heading = new Label("Available Courses");
        heading.setFont(FontLoader.getPoppinsBold(28));
        heading.setTextFill(ColorScheme.DARK_TEXT);
        
        Label subtitle = new Label("Browse and register for courses");
        subtitle.setFont(FontLoader.getOutfit(14));
        subtitle.setTextFill(ColorScheme.MEDIUM_TEXT);
        
        header.getChildren().addAll(heading, subtitle);
        return header;
    }

    private HBox buildRegistrationStatusBanner() {
        HBox banner = new HBox(15);
        banner.setAlignment(Pos.CENTER_LEFT);
        banner.setPadding(new Insets(16, 20, 16, 20));
        
        try {
            Semester currentSemester = semesterDao.getCurrentSemester();
            if (currentSemester != null) {
                RegistrationPeriod activePeriod = periodDao.getActiveRegistrationPeriod(currentSemester.getId());
                
                if (activePeriod != null && activePeriod.isActive()) {
                    // Registration is OPEN
                    banner.setStyle(
                        "-fx-background-color: " + ColorScheme.SUCCESS_50_HEX + "; " +
                        "-fx-background-radius: 8; " +
                        "-fx-border-color: " + ColorScheme.SUCCESS_200_HEX + "; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 8;"
                    );
                    
                    Label icon = new Label("✅");
                    icon.setFont(FontLoader.getOutfitBold(20));
                    
                    VBox textBox = new VBox(4);
                    Label title = new Label("Registration is OPEN");
                    title.setFont(FontLoader.getOutfitSemiBold(14));
                    title.setTextFill(ColorScheme.SUCCESS_700);
                    
                    Label info = new Label("You can register for courses until " + activePeriod.getEndDate().toLocalDate());
                    info.setFont(FontLoader.getOutfit(12));
                    info.setTextFill(ColorScheme.SUCCESS_600);
                    
                    textBox.getChildren().addAll(title, info);
                    banner.getChildren().addAll(icon, textBox);
                } else {
                    // Registration is CLOSED
                    banner.setStyle(
                        "-fx-background-color: " + ColorScheme.ERROR_50_HEX + "; " +
                        "-fx-background-radius: 8; " +
                        "-fx-border-color: " + ColorScheme.ERROR_200_HEX + "; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 8;"
                    );
                    
                    Label icon = new Label("🔒");
                    icon.setFont(FontLoader.getOutfitBold(20));
                    
                    VBox textBox = new VBox(4);
                    Label title = new Label("Registration is CLOSED");
                    title.setFont(FontLoader.getOutfitSemiBold(14));
                    title.setTextFill(ColorScheme.ERROR_700);
                    
                    Label info = new Label("Course registration is currently not available. Please check back later.");
                    info.setFont(FontLoader.getOutfit(12));
                    info.setTextFill(ColorScheme.ERROR_600);
                    
                    textBox.getChildren().addAll(title, info);
                    banner.getChildren().addAll(icon, textBox);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking registration status: " + e.getMessage());
        }
        
        return banner;
    }

    private HBox buildFilters() {
        HBox filters = new HBox(15);
        filters.setAlignment(Pos.CENTER_LEFT);
        
        // Semester filter
        Label semesterLabel = new Label("Semester:");
        semesterLabel.setFont(FontLoader.getOutfitMedium(14));
        semesterLabel.setTextFill(ColorScheme.DARK_TEXT);
        
        semesterFilter = new ComboBox<>();
        semesterFilter.getItems().addAll("All Semesters", "Semester I", "Semester II");
        semesterFilter.setValue("All Semesters");
        semesterFilter.setPrefWidth(180);
        semesterFilter.setOnAction(e -> refreshCourses());
        
        // Department filter
        Label deptLabel = new Label("Department:");
        deptLabel.setFont(FontLoader.getOutfitMedium(14));
        deptLabel.setTextFill(ColorScheme.DARK_TEXT);
        
        departmentFilter = new ComboBox<>();
        departmentFilter.getItems().addAll("All Departments", "Computer Science", "Software Engineering", 
            "Information Technology", "Electrical Engineering", "Mechanical Engineering");
        departmentFilter.setValue("All Departments");
        departmentFilter.setPrefWidth(200);
        departmentFilter.setOnAction(e -> refreshCourses());
        
        filters.getChildren().addAll(semesterLabel, semesterFilter, deptLabel, departmentFilter);
        
        return filters;
    }

    private void refreshCourses() {
        coursesContainer.getChildren().clear();
        
        try {
            List<Course> courses = courseDao.getAllCourses();
            
            // Apply filters
            String selectedSemester = semesterFilter != null ? semesterFilter.getValue() : "All Semesters";
            String selectedDept = departmentFilter != null ? departmentFilter.getValue() : "All Departments";
            
            for (Course course : courses) {
                // TODO: Add semester and department filtering when Course model is updated
                coursesContainer.getChildren().add(createCourseCard(course));
            }
            
            if (courses.isEmpty()) {
                Label emptyLabel = new Label("No courses available at the moment.");
                emptyLabel.setFont(FontLoader.getOutfit(14));
                emptyLabel.setTextFill(ColorScheme.MEDIUM_TEXT);
                emptyLabel.setPadding(new Insets(40));
                coursesContainer.getChildren().add(emptyLabel);
            }
            
        } catch (SQLException e) {
            showAlert("Error", "Failed to load courses: " + e.getMessage());
        }
    }

    private VBox createCourseCard(Course course) {
        VBox card = new VBox(16);
        card.setStyle(StyleConstants.card());
        card.setPadding(new Insets(24));
        
        // Header row
        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(headerRow, Priority.ALWAYS);
        
        VBox titleBox = new VBox(4);
        HBox.setHgrow(titleBox, Priority.ALWAYS);
        
        Label courseCode = new Label(course.getCode());
        courseCode.setFont(FontLoader.getOutfitSemiBold(16));
        courseCode.setTextFill(ColorScheme.PRIMARY_600);
        
        Label courseTitle = new Label(course.getTitle());
        courseTitle.setFont(FontLoader.getPoppinsBold(18));
        courseTitle.setTextFill(ColorScheme.DARK_TEXT);
        
        titleBox.getChildren().addAll(courseCode, courseTitle);
        
        // Credits badge
        Label creditsBadge = new Label(course.getCredits() + " Credits");
        creditsBadge.setFont(FontLoader.getOutfitSemiBold(12));
        creditsBadge.setTextFill(ColorScheme.PRIMARY_700);
        creditsBadge.setStyle(
            "-fx-background-color: " + ColorScheme.PRIMARY_50_HEX + "; " +
            "-fx-padding: 6 12; " +
            "-fx-background-radius: 12;"
        );
        
        headerRow.getChildren().addAll(titleBox, creditsBadge);
        
        // Course details
        HBox detailsRow = new HBox(30);
        detailsRow.setAlignment(Pos.CENTER_LEFT);
        
        detailsRow.getChildren().addAll(
            createDetailItem("👨‍🏫", "Instructor", course.getInstructorName() != null ? course.getInstructorName() : "TBA"),
            createDetailItem("👥", "Capacity", course.getCapacity() + " students"),
            createDetailItem("📚", "Department", "Computer Science") // TODO: Add department to Course model
        );
        
        // Action button
        Button registerBtn = new Button("Register for Course");
        registerBtn.setFont(FontLoader.getOutfitSemiBold(14));
        registerBtn.setTextFill(Color.WHITE);
        registerBtn.setPrefHeight(40);
        registerBtn.setPrefWidth(180);
        registerBtn.setStyle(ColorScheme.getPrimaryButtonStyle());
        registerBtn.setOnMouseEntered(e -> registerBtn.setStyle(ColorScheme.getPrimaryButtonHoverStyle()));
        registerBtn.setOnMouseExited(e -> registerBtn.setStyle(ColorScheme.getPrimaryButtonStyle()));
        registerBtn.setOnAction(e -> registerForCourse(course));
        
        // Check if already registered
        try {
            if (registrationDao.isStudentRegisteredForCourse(currentStudent.getId(), course.getId())) {
                registerBtn.setText("Already Registered");
                registerBtn.setDisable(true);
                registerBtn.setStyle(
                    "-fx-background-color: " + ColorScheme.GRAY_300_HEX + "; " +
                    "-fx-background-radius: 8; " +
                    "-fx-cursor: default;"
                );
            }
        } catch (SQLException e) {
            System.err.println("Error checking registration status: " + e.getMessage());
        }
        
        card.getChildren().addAll(headerRow, detailsRow, registerBtn);
        
        return card;
    }

    private VBox createDetailItem(String icon, String label, String value) {
        VBox item = new VBox(4);
        
        HBox labelRow = new HBox(6);
        labelRow.setAlignment(Pos.CENTER_LEFT);
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(FontLoader.getOutfitBold(14));
        
        Label textLabel = new Label(label);
        textLabel.setFont(FontLoader.getOutfit(12));
        textLabel.setTextFill(ColorScheme.MEDIUM_TEXT);
        
        labelRow.getChildren().addAll(iconLabel, textLabel);
        
        Label valueLabel = new Label(value);
        valueLabel.setFont(FontLoader.getOutfitSemiBold(14));
        valueLabel.setTextFill(ColorScheme.DARK_TEXT);
        
        item.getChildren().addAll(labelRow, valueLabel);
        
        return item;
    }

    private void registerForCourse(Course course) {
        // Step 4: System validates
        try {
            // Check if registration is open
            Semester currentSemester = semesterDao.getCurrentSemester();
            if (currentSemester == null) {
                showAlert("Registration Closed", "No active semester found.");
                return;
            }
            
            RegistrationPeriod activePeriod = periodDao.getActiveRegistrationPeriod(currentSemester.getId());
            if (activePeriod == null || !activePeriod.isActive()) {
                showAlert("Registration Closed", "Course registration is currently not available.");
                return;
            }
            
            // Check for duplicate registration
            if (registrationDao.isStudentRegisteredForCourse(currentStudent.getId(), course.getId())) {
                showAlert("Already Registered", "You are already registered for this course.");
                return;
            }
            
            // Check credit hour limit (e.g., max 18 credits per semester)
            int currentCredits = registrationDao.getTotalCreditsForStudent(currentStudent.getId());
            if (currentCredits + course.getCredits() > 18) {
                showAlert("Credit Limit Exceeded", 
                    "Registering for this course would exceed the maximum credit limit of 18 credits per semester.\n\n" +
                    "Current credits: " + currentCredits + "\n" +
                    "Course credits: " + course.getCredits() + "\n" +
                    "Total would be: " + (currentCredits + course.getCredits()));
                return;
            }
            
            // TODO: Check prerequisites when implemented
            
            // Confirm registration
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Registration");
            confirm.setHeaderText("Register for " + course.getCode() + "?");
            confirm.setContentText(
                "Course: " + course.getTitle() + "\n" +
                "Credits: " + course.getCredits() + "\n" +
                "Instructor: " + (course.getInstructorName() != null ? course.getInstructorName() : "TBA") + "\n\n" +
                "Your registration will be submitted for approval."
            );
            
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        // Step 5: Submit registration request with PENDING status
                        registrationDao.createRegistration(currentStudent.getId(), course.getId(), currentSemester.getId());
                        
                        Alert success = new Alert(Alert.AlertType.INFORMATION);
                        success.setTitle("Registration Submitted");
                        success.setHeaderText("Success!");
                        success.setContentText(
                            "Your registration request has been submitted.\n\n" +
                            "Status: PENDING\n\n" +
                            "Your department head will review and approve your request."
                        );
                        success.showAndWait();
                        
                        refreshCourses();
                        
                    } catch (SQLException e) {
                        showAlert("Error", "Failed to submit registration: " + e.getMessage());
                    }
                }
            });
            
        } catch (SQLException e) {
            showAlert("Error", "Failed to validate registration: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
