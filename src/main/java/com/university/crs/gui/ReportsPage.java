package com.university.crs.gui;

import com.university.crs.dao.CourseV2Dao;
import com.university.crs.dao.RegistrationDao;
import com.university.crs.dao.StudentV2Dao;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reports page — enrollment summary with clean table layout.
 */
public class ReportsPage {

    private final StudentV2Dao studentDao = new StudentV2Dao();
    private final CourseV2Dao courseDao = new CourseV2Dao();
    private final RegistrationDao registrationDao = new RegistrationDao();

    public Node build() {
        VBox page = new VBox(30);
        page.setPadding(new Insets(40, 50, 40, 50));
        page.setStyle("-fx-background-color: " + ColorScheme.BACKGROUND_HEX + ";");

        // Header with title
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label heading = new Label("Reports");
        heading.setFont(FontLoader.getPoppinsBold(28));
        heading.setTextFill(ColorScheme.DARK_TEXT);
        
        header.getChildren().add(heading);

        // Summary stats cards
        HBox statsCards = new HBox(25);
        statsCards.getChildren().addAll(
            createStatCard("📊", "Total Students", getTotalStudents(), ColorScheme.LIGHT_BLUE_HEX),
            createStatCard("📚", "Total Courses", getTotalCourses(), ColorScheme.SUCCESS_HEX),
            createStatCard("📋", "Total Enrollments", getTotalEnrollments(), ColorScheme.PURPLE_HEX)
        );

        // Enrollment summary table
        VBox tableContainer = buildEnrollmentTable();

        page.getChildren().addAll(header, statsCards, tableContainer);
        
        ScrollPane scrollPane = new ScrollPane(page);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + ColorScheme.BACKGROUND_HEX + "; -fx-background-color: " + ColorScheme.BACKGROUND_HEX + ";");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        return scrollPane;
    }

    private VBox createStatCard(String icon, String title, String value, String color) {
        VBox card = new VBox(12);
        card.setPrefSize(280, 120);
        card.setAlignment(Pos.TOP_LEFT);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 12; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 15, 0, 0, 3);"
        );

        // Icon with colored background
        StackPane iconContainer = new StackPane();
        iconContainer.setPrefSize(50, 50);
        iconContainer.setStyle(
            "-fx-background-color: " + color + "; " +
            "-fx-background-radius: 10;"
        );
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(FontLoader.getInter(26));
        // Don't set text fill for emoji icons - they should display in their natural colors
        iconContainer.getChildren().add(iconLabel);

        // Title
        Label titleLabel = new Label(title);
        titleLabel.setFont(FontLoader.getInter(13));
        titleLabel.setTextFill(Color.BLACK);

        // Value
        Label valueLabel = new Label(value);
        valueLabel.setFont(FontLoader.getPoppinsBold(32));
        valueLabel.setTextFill(ColorScheme.DARK_TEXT);

        card.getChildren().addAll(iconContainer, titleLabel, valueLabel);
        return card;
    }

    private VBox buildEnrollmentTable() {
        VBox container = new VBox();
        container.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 12; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 15, 0, 0, 3);"
        );
        container.setPadding(new Insets(25));

        // Section title
        Label sectionTitle = new Label("Enrollment Summary");
        sectionTitle.setFont(FontLoader.getPoppinsBold(20));
        sectionTitle.setTextFill(ColorScheme.DARK_TEXT);
        VBox.setMargin(sectionTitle, new Insets(0, 0, 20, 0));

        // Table header
        HBox headerRow = new HBox();
        headerRow.setSpacing(20);
        headerRow.setPadding(new Insets(0, 0, 15, 0));
        headerRow.setStyle("-fx-border-color: " + ColorScheme.SOFT_GRAY_HEX + "; -fx-border-width: 0 0 1 0;");
        
        Label col1 = createHeaderLabel("Course Code", 150);
        Label col2 = createHeaderLabel("Course Name", 350);
        Label col3 = createHeaderLabel("Enrolled", 120);
        Label col4 = createHeaderLabel("Capacity", 120);
        Label col5 = createHeaderLabel("Status", 150);
        
        headerRow.getChildren().addAll(col1, col2, col3, col4, col5);

        // Table rows
        VBox rows = new VBox(0);
        loadEnrollmentData(rows);

        container.getChildren().addAll(sectionTitle, headerRow, rows);
        return container;
    }

    private Label createHeaderLabel(String text, double width) {
        Label label = new Label(text);
        label.setFont(FontLoader.getInter(13));
        label.setTextFill(Color.BLACK);
        label.setStyle("-fx-font-weight: 600;");
        label.setPrefWidth(width);
        return label;
    }

    private void loadEnrollmentData(VBox rows) {
        try {
            // Get all courses
            var courses = courseDao.getAllCourses();
            
            if (courses.isEmpty()) {
                Label emptyLabel = new Label("No courses available");
                emptyLabel.setFont(FontLoader.getInter(14));
                emptyLabel.setTextFill(ColorScheme.MEDIUM_TEXT);
                emptyLabel.setPadding(new Insets(20, 0, 0, 0));
                rows.getChildren().add(emptyLabel);
                return;
            }
            
            // Get enrollment counts for each course
            Map<Integer, Integer> enrollmentCounts = new HashMap<>();
            var registrations = registrationDao.getAllRegistrations();
            
            for (var reg : registrations) {
                if ("APPROVED".equals(reg.getStatus())) {
                    enrollmentCounts.merge(reg.getCourseId(), 1, Integer::sum);
                }
            }
            
            // Create rows for each course
            for (var course : courses) {
                int enrolled = enrollmentCounts.getOrDefault(course.getId(), 0);
                rows.getChildren().add(createEnrollmentRow(
                    course.getCourseCode(),
                    course.getTitle(),
                    enrolled,
                    course.getCapacity()
                ));
            }
            
        } catch (SQLException e) {
            Label errorLabel = new Label("Error loading enrollment data: " + e.getMessage());
            errorLabel.setFont(FontLoader.getInter(14));
            errorLabel.setTextFill(ColorScheme.ERROR_600);
            errorLabel.setPadding(new Insets(20, 0, 0, 0));
            rows.getChildren().add(errorLabel);
            e.printStackTrace();
        }
    }

    private HBox createEnrollmentRow(String code, String title, int enrolled, int capacity) {
        HBox row = new HBox();
        row.setSpacing(20);
        row.setPadding(new Insets(15, 0, 15, 0));
        row.setStyle("-fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0;");
        
        Label col1 = createCellLabel(code, 150);
        Label col2 = createCellLabel(title, 350);
        Label col3 = createCellLabel(String.valueOf(enrolled), 120);
        Label col4 = createCellLabel(String.valueOf(capacity), 120);
        
        // Status badge
        HBox statusBox = new HBox();
        statusBox.setPrefWidth(150);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        
        Label statusLabel = new Label();
        statusLabel.setFont(FontLoader.getInter(12));
        statusLabel.setPadding(new Insets(5, 12, 5, 12));
        statusLabel.setStyle("-fx-background-radius: 12;");
        
        double fillPercentage = capacity > 0 ? (double) enrolled / capacity * 100 : 0;
        
        if (fillPercentage >= 90) {
            statusLabel.setText("Full");
            statusLabel.setStyle(
                "-fx-background-color: rgba(234, 84, 85, 0.15); " +
                "-fx-text-fill: " + ColorScheme.ERROR_600_HEX + "; " +
                "-fx-background-radius: 12; " +
                "-fx-font-weight: 600;"
            );
        } else if (fillPercentage >= 70) {
            statusLabel.setText("Filling");
            statusLabel.setStyle(
                "-fx-background-color: rgba(255, 159, 67, 0.15); " +
                "-fx-text-fill: " + ColorScheme.WARNING_600_HEX + "; " +
                "-fx-background-radius: 12; " +
                "-fx-font-weight: 600;"
            );
        } else {
            statusLabel.setText("Available");
            statusLabel.setStyle(
                "-fx-background-color: rgba(40, 199, 111, 0.15); " +
                "-fx-text-fill: " + ColorScheme.SUCCESS_600_HEX + "; " +
                "-fx-background-radius: 12; " +
                "-fx-font-weight: 600;"
            );
        }
        
        statusBox.getChildren().add(statusLabel);
        
        row.getChildren().addAll(col1, col2, col3, col4, statusBox);
        
        // Hover effect
        row.setOnMouseEntered(e -> row.setStyle(
            "-fx-background-color: #f9fafb; " +
            "-fx-border-color: #f3f4f6; " +
            "-fx-border-width: 0 0 1 0;"
        ));
        row.setOnMouseExited(e -> row.setStyle(
            "-fx-border-color: #f3f4f6; " +
            "-fx-border-width: 0 0 1 0;"
        ));
        
        return row;
    }

    private Label createCellLabel(String text, double width) {
        Label label = new Label(text);
        label.setFont(FontLoader.getInter(14));
        label.setTextFill(Color.BLACK);
        label.setPrefWidth(width);
        return label;
    }

    private String getTotalStudents() {
        try {
            return String.valueOf(studentDao.getAllStudents().size());
        } catch (SQLException e) {
            return "0";
        }
    }

    private String getTotalCourses() {
        try {
            return String.valueOf(courseDao.getAllCourses().size());
        } catch (SQLException e) {
            return "0";
        }
    }

    private String getTotalEnrollments() {
        try {
            // Count only approved registrations
            return String.valueOf(registrationDao.getAllRegistrations().stream()
                .filter(r -> "APPROVED".equals(r.getStatus()))
                .count());
        } catch (SQLException e) {
            return "0";
        }
    }
}
