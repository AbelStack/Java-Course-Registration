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
 * Department Head Students Page
 * Shows only students from the department head's department
 */
public class DeptHeadStudentsPage {

    private final Stage stage;
    private final User user;
    private final StudentV2Dao studentDao = new StudentV2Dao();
    private final RegistrationDao registrationDao = new RegistrationDao();
    
    private VBox mainContainer;

    public DeptHeadStudentsPage(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
    }

    public Node build() {
        mainContainer = new VBox(30);
        mainContainer.setPadding(new Insets(40, 50, 40, 50));
        mainContainer.setStyle("-fx-background-color: " + ColorScheme.BACKGROUND_HEX + ";");

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        
        VBox headerText = new VBox(8);
        HBox.setHgrow(headerText, Priority.ALWAYS);
        
        Label heading = new Label("Department Students");
        heading.setFont(FontLoader.getPoppinsBold(28));
        heading.setTextFill(ColorScheme.DARK_TEXT);
        
        Label subtitle = new Label("View and monitor students in your department");
        subtitle.setFont(FontLoader.getOutfit(14));
        subtitle.setTextFill(ColorScheme.MEDIUM_TEXT);
        
        headerText.getChildren().addAll(heading, subtitle);
        header.getChildren().add(headerText);

        // Stats cards
        HBox statsCards = buildStatsCards();

        // Table container
        VBox tableContainer = buildTableContainer();

        mainContainer.getChildren().addAll(header, statsCards, tableContainer);
        
        ScrollPane scrollPane = new ScrollPane(mainContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + ColorScheme.BACKGROUND_HEX + "; -fx-background-color: " + ColorScheme.BACKGROUND_HEX + ";");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        return scrollPane;
    }

    private HBox buildStatsCards() {
        HBox statsRow = new HBox(20);
        statsRow.setAlignment(Pos.CENTER_LEFT);
        
        try {
            // Get department head's department
            String deptHeadDepartment = user.getDepartment();
            
            // Get all students in this department
            List<StudentV2> allStudents = studentDao.getAllStudents();
            List<StudentV2> deptStudents = allStudents.stream()
                .filter(s -> s.getDepartmentName().equals(deptHeadDepartment))
                .collect(Collectors.toList());
            
            int totalStudents = deptStudents.size();
            
            // Count students by year level
            long year1 = deptStudents.stream().filter(s -> s.getYearLevel() == 1).count();
            long year2 = deptStudents.stream().filter(s -> s.getYearLevel() == 2).count();
            long year3 = deptStudents.stream().filter(s -> s.getYearLevel() == 3).count();
            long year4 = deptStudents.stream().filter(s -> s.getYearLevel() == 4).count();
            
            statsRow.getChildren().addAll(
                createStatCard("👥", "Total Students", String.valueOf(totalStudents), ColorScheme.PRIMARY_500_HEX, ColorScheme.PRIMARY_50_HEX),
                createStatCard("1️⃣", "Year 1", String.valueOf(year1), ColorScheme.SUCCESS_500_HEX, ColorScheme.SUCCESS_50_HEX),
                createStatCard("2️⃣", "Year 2", String.valueOf(year2), ColorScheme.WARNING_500_HEX, ColorScheme.WARNING_50_HEX),
                createStatCard("3️⃣", "Year 3", String.valueOf(year3), ColorScheme.ERROR_500_HEX, ColorScheme.ERROR_50_HEX),
                createStatCard("4️⃣", "Year 4", String.valueOf(year4), ColorScheme.BRAND_500_HEX, ColorScheme.BRAND_50_HEX)
            );
            
        } catch (SQLException e) {
            System.err.println("Error loading student stats: " + e.getMessage());
        }
        
        return statsRow;
    }

    private VBox createStatCard(String icon, String label, String value, String iconColor, String bgColor) {
        VBox card = new VBox(12);
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 12; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 15, 0, 0, 3); " +
            "-fx-padding: 20;"
        );
        card.setPrefWidth(160);
        card.setAlignment(Pos.TOP_LEFT);
        
        // Icon
        Label iconLabel = new Label(icon);
        iconLabel.setFont(FontLoader.getOutfitBold(28));
        iconLabel.setStyle(
            "-fx-background-color: " + bgColor + "; " +
            "-fx-padding: 10; " +
            "-fx-background-radius: 10;"
        );
        
        // Value
        Label valueLabel = new Label(value);
        valueLabel.setFont(FontLoader.getPoppinsBold(28));
        valueLabel.setTextFill(ColorScheme.DARK_TEXT);
        
        // Label
        Label textLabel = new Label(label);
        textLabel.setFont(FontLoader.getOutfit(13));
        textLabel.setTextFill(ColorScheme.MEDIUM_TEXT);
        
        card.getChildren().addAll(iconLabel, valueLabel, textLabel);
        
        return card;
    }

    private VBox buildTableContainer() {
        VBox container = new VBox();
        container.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 12; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 15, 0, 0, 3);"
        );
        container.setPadding(new Insets(25));

        // Table header
        HBox headerRow = new HBox();
        headerRow.setSpacing(15);
        headerRow.setPadding(new Insets(0, 0, 15, 0));
        headerRow.setStyle("-fx-border-color: " + ColorScheme.SOFT_GRAY_HEX + "; -fx-border-width: 0 0 1 0;");
        
        Label col1 = createHeaderLabel("Student ID", 140);
        Label col2 = createHeaderLabel("Name", 200);
        Label col3 = createHeaderLabel("Email", 220);
        Label col4 = createHeaderLabel("Year", 80);
        Label col5 = createHeaderLabel("GPA", 80);
        Label col6 = createHeaderLabel("Registrations", 120);
        Label col7 = createHeaderLabel("Actions", 100);
        
        headerRow.getChildren().addAll(col1, col2, col3, col4, col5, col6, col7);

        // Table rows
        VBox rows = new VBox(0);
        refreshTableRows(rows);

        container.getChildren().addAll(headerRow, rows);
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

    private void refreshTableRows(VBox rows) {
        rows.getChildren().clear();
        try {
            // Get department head's department
            String deptHeadDepartment = user.getDepartment();
            
            // Get all students in this department
            List<StudentV2> allStudents = studentDao.getAllStudents();
            List<StudentV2> deptStudents = allStudents.stream()
                .filter(s -> s.getDepartmentName().equals(deptHeadDepartment))
                .collect(Collectors.toList());
            
            for (StudentV2 student : deptStudents) {
                rows.getChildren().add(createTableRow(student));
            }
            
            if (deptStudents.isEmpty()) {
                Label emptyLabel = new Label("No students found in your department.");
                emptyLabel.setFont(FontLoader.getOutfit(14));
                emptyLabel.setTextFill(ColorScheme.MEDIUM_TEXT);
                emptyLabel.setPadding(new Insets(40));
                rows.getChildren().add(emptyLabel);
            }
        } catch (SQLException e) {
            System.err.println("Error loading students: " + e.getMessage());
        }
    }

    private HBox createTableRow(StudentV2 student) {
        HBox row = new HBox();
        row.setSpacing(15);
        row.setPadding(new Insets(15, 0, 15, 0));
        row.setStyle("-fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0;");
        
        Label col1 = createCellLabel(student.getStudentId(), 140);
        Label col2 = createCellLabel(student.getName(), 200);
        Label col3 = createCellLabel(student.getEmail(), 220);
        Label col4 = createCellLabel("Year " + student.getYearLevel(), 80);
        Label col5 = createCellLabel(String.format("%.2f", student.getGpa()), 80);
        
        // Get registration count
        Label col6 = createCellLabel("Loading...", 120);
        try {
            List<Registration> registrations = registrationDao.getRegistrationsByStudent(student.getId());
            long approvedCount = registrations.stream()
                .filter(r -> r.getStatus().equals("APPROVED"))
                .count();
            col6.setText(approvedCount + " courses");
        } catch (SQLException e) {
            col6.setText("N/A");
        }
        
        // Actions (View details button)
        HBox actions = new HBox(10);
        actions.setPrefWidth(100);
        actions.setAlignment(Pos.CENTER_LEFT);
        
        Button viewBtn = new Button("👁️");
        viewBtn.setFont(FontLoader.getInter(18));
        viewBtn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 5;"
        );
        viewBtn.setOnMouseEntered(e -> viewBtn.setStyle(
            "-fx-background-color: rgba(74, 144, 226, 0.1); " +
            "-fx-cursor: hand; " +
            "-fx-padding: 5; " +
            "-fx-background-radius: 5;"
        ));
        viewBtn.setOnMouseExited(e -> viewBtn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 5;"
        ));
        viewBtn.setOnAction(e -> showStudentDetails(student));
        
        actions.getChildren().add(viewBtn);
        
        row.getChildren().addAll(col1, col2, col3, col4, col5, col6, actions);
        
        // Hover effect
        row.setOnMouseEntered(e -> row.setStyle(
            "-fx-background-color: #f9fafb; " +
            "-fx-border-color: #f3f4f6; " +
            "-fx-border-width: 0 0 1 0; " +
            "-fx-cursor: hand;"
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

    private void showStudentDetails(StudentV2 student) {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("Student Details");
        dialog.setHeaderText(student.getName());
        
        try {
            List<Registration> registrations = registrationDao.getRegistrationsByStudent(student.getId());
            long pendingCount = registrations.stream().filter(r -> r.getStatus().equals("PENDING")).count();
            long approvedCount = registrations.stream().filter(r -> r.getStatus().equals("APPROVED")).count();
            long rejectedCount = registrations.stream().filter(r -> r.getStatus().equals("REJECTED")).count();
            
            String content = String.format(
                "Student ID: %s\n" +
                "Email: %s\n" +
                "Department: %s\n" +
                "Year Level: %d\n" +
                "GPA: %.2f\n\n" +
                "Registration Summary:\n" +
                "  • Pending: %d\n" +
                "  • Approved: %d\n" +
                "  • Rejected: %d\n" +
                "  • Total: %d",
                student.getStudentId(),
                student.getEmail(),
                student.getDepartmentName(),
                student.getYearLevel(),
                student.getGpa(),
                pendingCount,
                approvedCount,
                rejectedCount,
                registrations.size()
            );
            
            dialog.setContentText(content);
        } catch (SQLException e) {
            dialog.setContentText("Error loading student details: " + e.getMessage());
        }
        
        dialog.showAndWait();
    }
}
