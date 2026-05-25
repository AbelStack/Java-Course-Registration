package com.university.crs.gui;

import com.university.crs.dao.StudentDao;
import com.university.crs.model.Student;
import com.university.crs.model.User;
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
    private final StudentDao studentDao = new StudentDao();
    private List<Student> allStudents;
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
        searchField.setPromptText("Search students...");
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
        
        Label col1 = createHeaderLabel("Student ID", 120);
        Label col2 = createHeaderLabel("Name", 250);
        Label col3 = createHeaderLabel("Email", 300);
        Label col4 = createHeaderLabel("Department", 150);
        Label col5 = createHeaderLabel("Actions", 120);
        
        headerRow.getChildren().addAll(col1, col2, col3, col4, col5);

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
            for (Student student : allStudents) {
                tableRowsContainer.getChildren().add(createTableRow(student));
            }
        } catch (SQLException e) {
            System.err.println("Error loading students: " + e.getMessage());
        }
    }

    private void filterStudents(String searchText) {
        tableRowsContainer.getChildren().clear();
        if (allStudents == null) return;

        List<Student> filtered = allStudents.stream()
            .filter(s -> searchText == null || searchText.isEmpty() ||
                s.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                s.getEmail().toLowerCase().contains(searchText.toLowerCase()) ||
                String.valueOf(s.getId()).contains(searchText))
            .collect(Collectors.toList());

        for (Student student : filtered) {
            tableRowsContainer.getChildren().add(createTableRow(student));
        }
    }

    private HBox createTableRow(Student student) {
        HBox row = new HBox();
        row.setSpacing(20);
        row.setPadding(new Insets(15, 0, 15, 0));
        row.setStyle("-fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0;");
        
        Label col1 = createCellLabel("S" + String.format("%03d", student.getId()), 120);
        Label col2 = createCellLabel(student.getName(), 250);
        Label col3 = createCellLabel(student.getEmail(), 300);
        Label col4 = createCellLabel(getDepartment(student), 150);
        
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
        
        row.getChildren().addAll(col1, col2, col3, col4, actions);
        
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

    private String getDepartment(Student student) {
        // Placeholder - you can add department field to Student model
        String[] departments = {"CS", "IT", "ECE", "ME", "EE"};
        return departments[student.getId() % departments.length];
    }

    private void showEditStudentDialog(Student student) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Student");
        dialog.setHeaderText("Update student details");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField(student.getName());
        TextField emailField = new TextField(student.getEmail());

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    String name = nameField.getText().trim();
                    String email = emailField.getText().trim();

                    if (name.isEmpty() || email.isEmpty()) {
                        showAlert("Error", "Name and email are required.");
                        return;
                    }

                    studentDao.updateStudent(student.getId(), name, email);
                    refreshTableRows();
                } catch (SQLException e) {
                    showAlert("Error", "Failed to update student: " + e.getMessage());
                }
            }
        });
    }

    private void deleteStudent(Student student) {
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
        nameField.setPromptText("e.g., John Doe");
        
        TextField emailField = new TextField();
        emailField.setPromptText("e.g., john.doe@university.edu");
        
        ComboBox<String> departmentCombo = new ComboBox<>();
        departmentCombo.getItems().addAll("Computer Science", "Software Engineering", "Information Technology", "Electrical Engineering", "Mechanical Engineering");
        departmentCombo.setPromptText("Select Department");
        
        ComboBox<String> yearCombo = new ComboBox<>();
        yearCombo.getItems().addAll("2024", "2025", "2026", "2027");
        yearCombo.setPromptText("Select Year");
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Auto-generated (e.g., SWE-2026-001)");
        usernameField.setEditable(false);
        usernameField.setStyle("-fx-background-color: #f0f0f0;");
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Initial password");

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
                String deptCode = getDepartmentCode(newVal);
                String year = yearCombo.getValue();
                // TODO: Get next sequence number from database
                usernameField.setText(deptCode + "-" + year + "-001");
            }
        });
        
        yearCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && departmentCombo.getValue() != null) {
                String deptCode = getDepartmentCode(departmentCombo.getValue());
                // TODO: Get next sequence number from database
                usernameField.setText(deptCode + "-" + newVal + "-001");
            }
        });

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String department = departmentCombo.getValue();
                String year = yearCombo.getValue();
                String studentId = usernameField.getText().trim();
                String password = passwordField.getText().trim();

                if (name.isEmpty() || email.isEmpty() || department == null || year == null || password.isEmpty()) {
                    showAlert("Error", "All fields are required.");
                    return;
                }

                try {
                    // Create user account for login
                    com.university.crs.dao.UserDao userDao = new com.university.crs.dao.UserDao();
                    
                    // Check if username already exists
                    if (userDao.usernameExists(studentId)) {
                        showAlert("Error", "Student ID already exists. Please use a different ID.");
                        return;
                    }
                    
                    // Create user account (username = studentId, role = STUDENT, approved = true since admin creates it)
                    userDao.createAccount(studentId, password, "STUDENT", name, email, department);
                    
                    // Also add to students table
                    studentDao.addStudent(name, email);
                    
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Success");
                    success.setHeaderText("Student created successfully!");
                    success.setContentText("Student ID: " + studentId + "\nPassword: " + password + "\n\nPlease provide these credentials to the student.");
                    success.showAndWait();
                    
                    refreshTableRows();
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
