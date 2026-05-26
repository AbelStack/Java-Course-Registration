package com.university.crs.gui;

import com.university.crs.dao.StudentV2Dao;
import com.university.crs.model.StudentV2;
import com.university.crs.model.User;
import com.university.crs.util.ValidationUtil;
import com.university.crs.util.ValidationUtil.ValidationResult;
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
 * Students management page — clean table with search and inline actions.
 */
public class StudentsPage {

    private final Stage stage;
    private final User  user;
    private final StudentV2Dao studentDao = new StudentV2Dao();
    private List<StudentV2> allStudents;
    private VBox tableRowsContainer;

    public StudentsPage(Stage stage, User user) {
        this.stage = stage;
        this.user  = user;
    }

    public Node build() {
        VBox page = new VBox(30);
        page.setPadding(new Insets(40, 50, 40, 50));
        page.setStyle("-fx-background-color: " + ColorScheme.BACKGROUND_HEX + ";");

        // Header with title and Add button
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(header, Priority.ALWAYS);
        
        Label heading = new Label("Manage Students");
        heading.setFont(FontLoader.getPoppinsBold(28));
        heading.setTextFill(ColorScheme.DARK_TEXT);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button addStudentBtn = new Button("+ Add Student");
        addStudentBtn.setFont(FontLoader.getPoppinsBold(14));
        addStudentBtn.setTextFill(Color.WHITE);
        addStudentBtn.setPrefHeight(45);
        addStudentBtn.setPrefWidth(170);
        addStudentBtn.setStyle(StyleConstants.buttonPrimary());
        addStudentBtn.setOnMouseEntered(e -> addStudentBtn.setStyle(StyleConstants.buttonPrimaryHover()));
        addStudentBtn.setOnMouseExited(e -> addStudentBtn.setStyle(StyleConstants.buttonPrimary()));
        addStudentBtn.setOnAction(e -> showAddStudentDialog());
        
        header.getChildren().addAll(heading, spacer, addStudentBtn);

        // Search bar
        HBox searchBox = buildSearchBar();

        // Table container
        VBox tableContainer = buildTableContainer();

        page.getChildren().addAll(header, searchBox, tableContainer);
        
        ScrollPane scrollPane = new ScrollPane(page);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + ColorScheme.BACKGROUND_HEX + "; -fx-background-color: " + ColorScheme.BACKGROUND_HEX + ";");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        return scrollPane;
    }

    private HBox buildSearchBar() {
        HBox searchBox = new HBox();
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(15, 20, 15, 20));
        searchBox.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 12; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 15, 0, 0, 3);"
        );

        TextField searchField = new TextField();
        searchField.setPrefHeight(40);
        searchField.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-border-color: transparent; " +
            "-fx-font-size: 14px; " +
            "-fx-font-family: " + FontLoader.getInterFontFamily() + "; " +
            "-fx-prompt-text-fill: #999999;"
        );
        HBox.setHgrow(searchField, Priority.ALWAYS);

        // Search icon
        Label searchIcon = new Label("🔍");
        searchIcon.setFont(FontLoader.getInter(18));
        searchIcon.setStyle("-fx-text-fill: #999999;");

        searchBox.getChildren().addAll(searchField, searchIcon);

        // Search functionality
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filterStudents(newVal);
        });

        return searchBox;
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
        headerRow.setSpacing(20);
        headerRow.setPadding(new Insets(0, 0, 15, 0));
        headerRow.setStyle("-fx-border-color: " + ColorScheme.SOFT_GRAY_HEX + "; -fx-border-width: 0 0 1 0;");
        
        Label col1 = createHeaderLabel("Student ID", 150);
        Label col2 = createHeaderLabel("Name", 200);
        Label col3 = createHeaderLabel("Email", 250);
        Label col4 = createHeaderLabel("Department", 180);
        Label col5 = createHeaderLabel("Year", 80);
        Label col6 = createHeaderLabel("Actions", 120);
        
        headerRow.getChildren().addAll(col1, col2, col3, col4, col5, col6);

        // Table rows container
        tableRowsContainer = new VBox(0);
        refreshTableRows();

        container.getChildren().addAll(headerRow, tableRowsContainer);
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

    private void refreshTableRows() {
        tableRowsContainer.getChildren().clear();
        try {
            allStudents = studentDao.getAllStudents();
            for (StudentV2 student : allStudents) {
                tableRowsContainer.getChildren().add(createTableRow(student));
            }
        } catch (SQLException e) {
            System.err.println("Error loading students: " + e.getMessage());
        }
    }

    private void filterStudents(String searchText) {
        tableRowsContainer.getChildren().clear();
        if (allStudents == null) return;

        List<StudentV2> filtered = allStudents.stream()
            .filter(s -> searchText == null || searchText.isEmpty() ||
                s.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                s.getEmail().toLowerCase().contains(searchText.toLowerCase()) ||
                s.getStudentId().toLowerCase().contains(searchText.toLowerCase()) ||
                s.getDepartmentName().toLowerCase().contains(searchText.toLowerCase()))
            .collect(Collectors.toList());

        for (StudentV2 student : filtered) {
            tableRowsContainer.getChildren().add(createTableRow(student));
        }
    }

    private HBox createTableRow(StudentV2 student) {
        HBox row = new HBox();
        row.setSpacing(20);
        row.setPadding(new Insets(15, 0, 15, 0));
        row.setStyle("-fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0;");
        
        Label col1 = createCellLabel(student.getStudentId(), 150);
        Label col2 = createCellLabel(student.getName(), 200);
        Label col3 = createCellLabel(student.getEmail(), 250);
        Label col4 = createCellLabel(student.getDepartmentName(), 180);
        Label col5 = createCellLabel("Year " + student.getYearLevel(), 80);
        
        // Actions (Edit and Delete icons)
        HBox actions = new HBox(15);
        actions.setPrefWidth(120);
        actions.setAlignment(Pos.CENTER_LEFT);
        
        // Edit button
        Button editBtn = new Button("✏️");
        editBtn.setFont(FontLoader.getInter(18));
        editBtn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 5;"
        );
        editBtn.setOnMouseEntered(e -> editBtn.setStyle(
            "-fx-background-color: rgba(74, 144, 226, 0.1); " +
            "-fx-cursor: hand; " +
            "-fx-padding: 5; " +
            "-fx-background-radius: 5;"
        ));
        editBtn.setOnMouseExited(e -> editBtn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 5;"
        ));
        editBtn.setOnAction(e -> showEditStudentDialog(student));
        
        // Delete button
        Button deleteBtn = new Button("🗑️");
        deleteBtn.setFont(FontLoader.getInter(18));
        deleteBtn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 5;"
        );
        deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle(
            "-fx-background-color: rgba(234, 84, 85, 0.1); " +
            "-fx-cursor: hand; " +
            "-fx-padding: 5; " +
            "-fx-background-radius: 5;"
        ));
        deleteBtn.setOnMouseExited(e -> deleteBtn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 5;"
        ));
        deleteBtn.setOnAction(e -> deleteStudent(student));
        
        actions.getChildren().addAll(editBtn, deleteBtn);
        
        row.getChildren().addAll(col1, col2, col3, col4, col5, actions);
        
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

    private String getDepartment(StudentV2 student) {
        return student.getDepartmentName();
    }

    private void showEditStudentDialog(StudentV2 student) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Student");
        dialog.setHeaderText("Update student details");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField(student.getName());
        TextField emailField = new TextField(student.getEmail());
        ComboBox<Integer> yearCombo = new ComboBox<>();
        yearCombo.getItems().addAll(1, 2, 3, 4, 5);
        yearCombo.setValue(student.getYearLevel());

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Year Level:"), 0, 2);
        grid.add(yearCombo, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Validate name
                ValidationResult nameResult = ValidationUtil.validateName(nameField.getText());
                if (!nameResult.isValid()) {
                    showAlert("Validation Error", nameResult.getErrorMessage());
                    return;
                }
                
                // Validate email
                ValidationResult emailResult = ValidationUtil.validateEmail(emailField.getText());
                if (!emailResult.isValid()) {
                    showAlert("Validation Error", emailResult.getErrorMessage());
                    return;
                }
                
                // Validate year level
                Integer yearLevel = yearCombo.getValue();
                ValidationResult yearResult = ValidationUtil.validateYearLevel(yearLevel);
                if (!yearResult.isValid()) {
                    showAlert("Validation Error", yearResult.getErrorMessage());
                    return;
                }

                try {
                    String name = nameResult.getStringValue();
                    String email = emailResult.getStringValue();

                    studentDao.updateStudent(student.getId(), name, email, student.getDepartmentId(), yearLevel);
                    refreshTableRows();
                } catch (SQLException e) {
                    showAlert("Error", "Failed to update student: " + e.getMessage());
                }
            }
        });
    }

    private void deleteStudent(StudentV2 student) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Student");
        confirm.setHeaderText("Are you sure you want to delete this student?");
        confirm.setContentText("Student: " + student.getName() + " (" + student.getEmail() + ")");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    studentDao.deleteStudent(student.getId());
                    refreshTableRows();
                } catch (SQLException e) {
                    showAlert("Error", "Failed to delete student: " + e.getMessage());
                }
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

    private void showAddStudentDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Student");
        dialog.setHeaderText("Create a new student account");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        
        TextField emailField = new TextField();
        
        ComboBox<String> departmentCombo = new ComboBox<>();
        departmentCombo.getItems().addAll("Computer Science", "Software Engineering", "Information Technology", "Electrical Engineering", "Mechanical Engineering");
        
        ComboBox<String> yearCombo = new ComboBox<>();
        yearCombo.getItems().addAll("2024", "2025", "2026", "2027");
        
        TextField usernameField = new TextField();
        usernameField.setEditable(false);
        usernameField.setStyle("-fx-background-color: #f0f0f0;");
        
        PasswordField passwordField = new PasswordField();

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Department:"), 0, 2);
        grid.add(departmentCombo, 1, 2);
        grid.add(new Label("Year:"), 0, 3);
        grid.add(yearCombo, 1, 3);
        grid.add(new Label("Student ID:"), 0, 4);
        grid.add(usernameField, 1, 4);
        grid.add(new Label("Password:"), 0, 5);
        grid.add(passwordField, 1, 5);
        
        Label noteLabel = new Label("Note: Student ID will be auto-generated based on department and year");
        noteLabel.setFont(FontLoader.getInter(11));
        noteLabel.setTextFill(ColorScheme.GRAY_600);
        noteLabel.setWrapText(true);
        grid.add(noteLabel, 0, 6, 2, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Auto-generate student ID when department and year are selected
        departmentCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && yearCombo.getValue() != null) {
                try {
                    String deptCode = getDepartmentCode(newVal);
                    String year = yearCombo.getValue();
                    int nextSeq = studentDao.getNextSequenceNumber(deptCode, year);
                    usernameField.setText(deptCode + "-" + year + "-" + String.format("%03d", nextSeq));
                } catch (SQLException e) {
                    usernameField.setText(getDepartmentCode(newVal) + "-" + yearCombo.getValue() + "-001");
                }
            }
        });
        
        yearCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && departmentCombo.getValue() != null) {
                try {
                    String deptCode = getDepartmentCode(departmentCombo.getValue());
                    int nextSeq = studentDao.getNextSequenceNumber(deptCode, newVal);
                    usernameField.setText(deptCode + "-" + newVal + "-" + String.format("%03d", nextSeq));
                } catch (SQLException e) {
                    usernameField.setText(getDepartmentCode(departmentCombo.getValue()) + "-" + newVal + "-001");
                }
            }
        });

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Validate name
                ValidationResult nameResult = ValidationUtil.validateName(nameField.getText());
                if (!nameResult.isValid()) {
                    showAlert("Validation Error", nameResult.getErrorMessage());
                    return;
                }
                
                // Validate email
                ValidationResult emailResult = ValidationUtil.validateEmail(emailField.getText());
                if (!emailResult.isValid()) {
                    showAlert("Validation Error", emailResult.getErrorMessage());
                    return;
                }
                
                // Validate department selection
                String departmentName = departmentCombo.getValue();
                if (departmentName == null || departmentName.isEmpty()) {
                    showAlert("Validation Error", "Please select a department");
                    return;
                }
                
                // Validate year selection
                String year = yearCombo.getValue();
                if (year == null || year.isEmpty()) {
                    showAlert("Validation Error", "Please select a year");
                    return;
                }
                
                // Validate password
                ValidationResult passwordResult = ValidationUtil.validatePassword(passwordField.getText());
                if (!passwordResult.isValid()) {
                    showAlert("Validation Error", passwordResult.getErrorMessage());
                    return;
                }

                String name = nameResult.getStringValue();
                String email = emailResult.getStringValue();
                String studentId = usernameField.getText().trim();
                String password = passwordResult.getStringValue();

                try {
                    com.university.crs.dao.UserDao userDao = new com.university.crs.dao.UserDao();
                    
                    // Check if username already exists
                    if (userDao.usernameExists(studentId)) {
                        showAlert("Error", "Student ID already exists. Please use a different ID.");
                        return;
                    }
                    
                    // Get department ID
                    int departmentId = studentDao.getDepartmentIdByName(departmentName);
                    
                    // Calculate year level based on current year and enrollment year
                    int currentYear = java.time.Year.now().getValue();
                    int enrollmentYear = Integer.parseInt(year);
                    int yearLevel = Math.max(1, currentYear - enrollmentYear + 1);
                    
                    // First, add to students_v2 table
                    int studentV2Id = studentDao.addStudent(studentId, name, email, departmentId, yearLevel);
                    
                    if (studentV2Id > 0) {
                        // Then create user account (auto-approved since admin creates it)
                        userDao.createAccountWithApproval(studentId, password, "STUDENT", name, email, departmentName, true, studentV2Id);
                        
                        Alert success = new Alert(Alert.AlertType.INFORMATION);
                        success.setTitle("Success");
                        success.setHeaderText("Student created successfully!");
                        success.setContentText("Student ID: " + studentId + 
                                             "\nPassword: " + password + 
                                             "\nStatus: Approved (Auto-approved by admin)" +
                                             "\n\nPlease provide these credentials to the student.");
                        success.showAndWait();
                        
                        refreshTableRows();
                    } else {
                        showAlert("Error", "Failed to create student record.");
                    }
                } catch (SQLException e) {
                    showAlert("Database Error", "Failed to create student: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
    
    private String getDepartmentCode(String departmentName) {
        switch (departmentName) {
            case "Software Engineering": return "SWE";
            case "Computer Science": return "CS";
            case "Information Technology": return "IT";
            case "Electrical Engineering": return "EE";
            case "Mechanical Engineering": return "ME";
            default: return "UNK";
        }
    }
}
