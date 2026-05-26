package com.university.crs.gui;

import com.university.crs.dao.*;
import com.university.crs.model.*;
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

/**
 * Courses management page — clean table with Add / Edit / Delete.
 */
public class CoursesPage {

    private final Stage stage;
    private final User  user;
    private final CourseV2Dao courseDao = new CourseV2Dao();
    private final DepartmentDao departmentDao = new DepartmentDao();
    private final SemesterDao semesterDao = new SemesterDao();
    private final InstructorDao instructorDao = new InstructorDao();
    private VBox mainContainer; // Store reference to main container

    public CoursesPage(Stage stage, User user) {
        this.stage = stage;
        this.user  = user;
    }

    public Node build() {
        mainContainer = new VBox(30);
        mainContainer.setPadding(new Insets(40, 50, 40, 50));
        mainContainer.setStyle("-fx-background-color: " + ColorScheme.BACKGROUND_HEX + ";");

        // Header with title and Add Course button
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label heading = new Label("Manage Courses");
        heading.setFont(FontLoader.getPoppinsBold(28));
        heading.setTextFill(ColorScheme.DARK_TEXT);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button addCourseBtn = new Button("+ Add Course");
        addCourseBtn.setFont(FontLoader.getPoppinsBold(14));
        addCourseBtn.setTextFill(ColorScheme.WHITE);
        addCourseBtn.setPrefHeight(45);
        addCourseBtn.setPrefWidth(150);
        addCourseBtn.setStyle(ColorScheme.getPrimaryButtonStyle());
        addCourseBtn.setOnMouseEntered(e -> addCourseBtn.setStyle(ColorScheme.getPrimaryButtonHoverStyle()));
        addCourseBtn.setOnMouseExited(e -> addCourseBtn.setStyle(ColorScheme.getPrimaryButtonStyle()));
        addCourseBtn.setOnAction(e -> showAddCourseDialog());
        
        header.getChildren().addAll(heading, spacer, addCourseBtn);

        // Table container
        VBox tableContainer = buildTableContainer();

        mainContainer.getChildren().addAll(header, tableContainer);
        
        ScrollPane scrollPane = new ScrollPane(mainContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + ColorScheme.BACKGROUND_HEX + "; -fx-background-color: " + ColorScheme.BACKGROUND_HEX + ";");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        return scrollPane;
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
        
        Label col1 = createHeaderLabel("Code", 100);
        Label col2 = createHeaderLabel("Title", 200);
        Label col3 = createHeaderLabel("Department", 140);
        Label col4 = createHeaderLabel("Semester", 100);
        Label col5 = createHeaderLabel("Year", 60);
        Label col6 = createHeaderLabel("Instructor", 140);
        Label col7 = createHeaderLabel("Credits", 70);
        Label col8 = createHeaderLabel("Capacity", 80);
        Label col9 = createHeaderLabel("Actions", 100);
        
        headerRow.getChildren().addAll(col1, col2, col3, col4, col5, col6, col7, col8, col9);

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
            List<CourseV2> courses = courseDao.getAllCourses();
            for (CourseV2 course : courses) {
                rows.getChildren().add(createTableRow(course, rows));
            }
        } catch (SQLException e) {
            System.err.println("Error loading courses: " + e.getMessage());
        }
    }

    private HBox createTableRow(CourseV2 course, VBox parentRows) {
        HBox row = new HBox();
        row.setSpacing(15);
        row.setPadding(new Insets(15, 0, 15, 0));
        row.setStyle("-fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0;");
        
        Label col1 = createCellLabel(course.getCourseCode(), 100);
        Label col2 = createCellLabel(course.getTitle(), 200);
        Label col3 = createCellLabel(course.getDepartmentName(), 140);
        Label col4 = createCellLabel(course.getSemesterName(), 100);
        Label col5 = createCellLabel(String.valueOf(course.getYearLevel()), 60);
        Label col6 = createCellLabel(course.getInstructorName() != null ? course.getInstructorName() : "TBA", 140);
        Label col7 = createCellLabel(String.valueOf(course.getCredits()), 70);
        Label col8 = createCellLabel(String.valueOf(course.getCapacity()), 80);
        
        // Actions (Edit and Delete icons)
        HBox actions = new HBox(15);
        actions.setPrefWidth(100);
        actions.setAlignment(Pos.CENTER_LEFT);
        
        // Edit button (pencil icon)
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
        editBtn.setOnAction(e -> showEditCourseDialog(course, parentRows));
        
        // Delete button (trash icon)
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
        deleteBtn.setOnAction(e -> deleteCourse(course, parentRows));
        
        actions.getChildren().addAll(editBtn, deleteBtn);
        
        row.getChildren().addAll(col1, col2, col3, col4, col5, col6, col7, col8, actions);
        
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

    private void showAddCourseDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New Course");
        dialog.setHeaderText("Enter course details");

        // Create form fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField codeField = new TextField();
        codeField.setPromptText("e.g., CS101");
        
        TextField titleField = new TextField();
        titleField.setPromptText("e.g., Introduction to Computer Science");
        
        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText("Course description (optional)");
        descriptionField.setPrefRowCount(2);
        
        TextField creditsField = new TextField();
        creditsField.setPromptText("e.g., 3");
        
        // Department dropdown - load from database
        ComboBox<String> departmentCombo = new ComboBox<>();
        try {
            List<Department> departments = departmentDao.getAllDepartments();
            for (Department dept : departments) {
                departmentCombo.getItems().add(dept.getId() + ": " + dept.getName());
            }
        } catch (SQLException e) {
            System.err.println("Error loading departments: " + e.getMessage());
        }
        departmentCombo.setPromptText("Select Department");
        
        // Year dropdown (year_level: 1, 2, 3, 4)
        ComboBox<String> yearCombo = new ComboBox<>();
        yearCombo.getItems().addAll("1", "2", "3", "4");
        yearCombo.setPromptText("Select Year Level");
        
        // Semester dropdown - load from database
        ComboBox<String> semesterCombo = new ComboBox<>();
        try {
            List<Semester> semesters = semesterDao.getAllSemesters();
            for (Semester semester : semesters) {
                semesterCombo.getItems().add(semester.getId() + ": " + semester.getSemesterName());
            }
        } catch (SQLException e) {
            System.err.println("Error loading semesters: " + e.getMessage());
        }
        semesterCombo.setPromptText("Select Semester");
        
        // Instructor dropdown (optional)
        ComboBox<String> instructorCombo = new ComboBox<>();
        instructorCombo.getItems().add("-- No Instructor --");
        try {
            List<Instructor> instructors = instructorDao.getAllInstructors();
            for (Instructor instructor : instructors) {
                instructorCombo.getItems().add(instructor.getId() + ": " + instructor.getName());
            }
        } catch (SQLException e) {
            System.err.println("Error loading instructors: " + e.getMessage());
        }
        instructorCombo.setValue("-- No Instructor --");
        instructorCombo.setPromptText("Select Instructor (optional)");
        
        TextField capacityField = new TextField();
        capacityField.setPromptText("e.g., 30");

        grid.add(new Label("Course Code:"), 0, 0);
        grid.add(codeField, 1, 0);
        grid.add(new Label("Course Title:"), 0, 1);
        grid.add(titleField, 1, 1);
        grid.add(new Label("Description:"), 0, 2);
        grid.add(descriptionField, 1, 2);
        grid.add(new Label("Credit Hours:"), 0, 3);
        grid.add(creditsField, 1, 3);
        grid.add(new Label("Department:"), 0, 4);
        grid.add(departmentCombo, 1, 4);
        grid.add(new Label("Year Level:"), 0, 5);
        grid.add(yearCombo, 1, 5);
        grid.add(new Label("Semester:"), 0, 6);
        grid.add(semesterCombo, 1, 6);
        grid.add(new Label("Instructor:"), 0, 7);
        grid.add(instructorCombo, 1, 7);
        grid.add(new Label("Capacity:"), 0, 8);
        grid.add(capacityField, 1, 8);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Validate course code
                ValidationResult codeResult = ValidationUtil.validateCourseCode(codeField.getText());
                if (!codeResult.isValid()) {
                    showAlert("Validation Error", codeResult.getErrorMessage());
                    return;
                }
                
                // Validate course title
                ValidationResult titleResult = ValidationUtil.validateCourseTitle(titleField.getText());
                if (!titleResult.isValid()) {
                    showAlert("Validation Error", titleResult.getErrorMessage());
                    return;
                }
                
                // Validate credits
                ValidationResult creditsResult = ValidationUtil.validateCredits(creditsField.getText());
                if (!creditsResult.isValid()) {
                    showAlert("Validation Error", creditsResult.getErrorMessage());
                    return;
                }
                
                // Validate department
                if (departmentCombo.getValue() == null) {
                    showAlert("Validation Error", "Please select a department.");
                    return;
                }
                
                // Validate year
                if (yearCombo.getValue() == null) {
                    showAlert("Validation Error", "Please select a year level.");
                    return;
                }
                
                // Validate semester
                if (semesterCombo.getValue() == null) {
                    showAlert("Validation Error", "Please select a semester.");
                    return;
                }
                
                // Validate capacity
                ValidationResult capacityResult = ValidationUtil.validateCapacity(capacityField.getText());
                if (!capacityResult.isValid()) {
                    showAlert("Validation Error", capacityResult.getErrorMessage());
                    return;
                }

                try {
                    String code = codeResult.getStringValue();
                    String title = titleResult.getStringValue();
                    String description = descriptionField.getText().trim();
                    int credits = creditsResult.getIntValue();
                    int capacity = capacityResult.getIntValue();
                    
                    // Extract department ID
                    int departmentId = Integer.parseInt(departmentCombo.getValue().split(":")[0]);
                    
                    // Extract year level
                    int yearLevel = Integer.parseInt(yearCombo.getValue());
                    
                    // Extract semester ID
                    int semesterId = Integer.parseInt(semesterCombo.getValue().split(":")[0]);
                    
                    // Extract instructor ID (optional)
                    Integer instructorId = null;
                    String selectedInstructor = instructorCombo.getValue();
                    if (selectedInstructor != null && !selectedInstructor.equals("-- No Instructor --")) {
                        instructorId = Integer.parseInt(selectedInstructor.split(":")[0]);
                    }

                    // Add course to courses_v2 table
                    courseDao.addCourse(code, title, description, departmentId, instructorId, 
                                       credits, capacity, semesterId, yearLevel);
                    
                    showSuccessAlert("Success", "Course added successfully!\n\nCourse: " + code + " - " + title);
                    refreshPage();
                } catch (SQLException e) {
                    showAlert("Database Error", "Failed to add course: " + e.getMessage());
                } catch (NumberFormatException e) {
                    showAlert("Error", "Invalid selection format: " + e.getMessage());
                }
            }
        });
    }

    private void showEditCourseDialog(CourseV2 course, VBox parentRows) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Course");
        dialog.setHeaderText("Update course details");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField codeField = new TextField(course.getCourseCode());
        TextField titleField = new TextField(course.getTitle());
        
        TextArea descriptionField = new TextArea(course.getDescription() != null ? course.getDescription() : "");
        descriptionField.setPrefRowCount(2);
        
        // Department dropdown
        ComboBox<String> departmentCombo = new ComboBox<>();
        try {
            List<Department> departments = departmentDao.getAllDepartments();
            for (Department dept : departments) {
                departmentCombo.getItems().add(dept.getId() + ": " + dept.getName());
            }
            departmentCombo.setValue(course.getDepartmentId() + ": " + course.getDepartmentName());
        } catch (SQLException e) {
            System.err.println("Error loading departments: " + e.getMessage());
        }
        
        // Year dropdown
        ComboBox<String> yearCombo = new ComboBox<>();
        yearCombo.getItems().addAll("1", "2", "3", "4");
        yearCombo.setValue(String.valueOf(course.getYearLevel()));
        
        // Semester dropdown
        ComboBox<String> semesterCombo = new ComboBox<>();
        try {
            List<Semester> semesters = semesterDao.getAllSemesters();
            for (Semester semester : semesters) {
                semesterCombo.getItems().add(semester.getId() + ": " + semester.getSemesterName());
            }
            semesterCombo.setValue(course.getSemesterId() + ": " + course.getSemesterName());
        } catch (SQLException e) {
            System.err.println("Error loading semesters: " + e.getMessage());
        }
        
        // Instructor dropdown
        ComboBox<String> instructorCombo = new ComboBox<>();
        instructorCombo.setPromptText("Select instructor (optional)");
        instructorCombo.getItems().add("-- No Instructor --");
        try {
            List<Instructor> instructors = instructorDao.getAllInstructors();
            for (Instructor instructor : instructors) {
                instructorCombo.getItems().add(instructor.getId() + ": " + instructor.getName());
            }
            // Set current instructor
            if (course.getInstructorId() != null) {
                instructorCombo.setValue(course.getInstructorId() + ": " + course.getInstructorName());
            } else {
                instructorCombo.setValue("-- No Instructor --");
            }
        } catch (SQLException e) {
            System.err.println("Error loading instructors: " + e.getMessage());
            instructorCombo.setValue("-- No Instructor --");
        }
        instructorCombo.setPrefWidth(250);
        
        TextField creditsField = new TextField(String.valueOf(course.getCredits()));
        TextField capacityField = new TextField(String.valueOf(course.getCapacity()));

        grid.add(new Label("Course Code:"), 0, 0);
        grid.add(codeField, 1, 0);
        grid.add(new Label("Course Title:"), 0, 1);
        grid.add(titleField, 1, 1);
        grid.add(new Label("Description:"), 0, 2);
        grid.add(descriptionField, 1, 2);
        grid.add(new Label("Department:"), 0, 3);
        grid.add(departmentCombo, 1, 3);
        grid.add(new Label("Year Level:"), 0, 4);
        grid.add(yearCombo, 1, 4);
        grid.add(new Label("Semester:"), 0, 5);
        grid.add(semesterCombo, 1, 5);
        grid.add(new Label("Instructor:"), 0, 6);
        grid.add(instructorCombo, 1, 6);
        grid.add(new Label("Credits:"), 0, 7);
        grid.add(creditsField, 1, 7);
        grid.add(new Label("Capacity:"), 0, 8);
        grid.add(capacityField, 1, 8);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Validate course code
                ValidationResult codeResult = ValidationUtil.validateCourseCode(codeField.getText());
                if (!codeResult.isValid()) {
                    showAlert("Validation Error", codeResult.getErrorMessage());
                    return;
                }
                
                // Validate course title
                ValidationResult titleResult = ValidationUtil.validateCourseTitle(titleField.getText());
                if (!titleResult.isValid()) {
                    showAlert("Validation Error", titleResult.getErrorMessage());
                    return;
                }
                
                // Validate department
                if (departmentCombo.getValue() == null) {
                    showAlert("Validation Error", "Please select a department.");
                    return;
                }
                
                // Validate year
                if (yearCombo.getValue() == null) {
                    showAlert("Validation Error", "Please select a year level.");
                    return;
                }
                
                // Validate semester
                if (semesterCombo.getValue() == null) {
                    showAlert("Validation Error", "Please select a semester.");
                    return;
                }
                
                // Get instructor ID from dropdown
                Integer instructorId = null;
                String selectedInstructor = instructorCombo.getValue();
                if (selectedInstructor != null && !selectedInstructor.equals("-- No Instructor --")) {
                    try {
                        instructorId = Integer.parseInt(selectedInstructor.split(":")[0]);
                    } catch (Exception e) {
                        // Invalid format, leave as null
                    }
                }
                
                // Validate credits
                ValidationResult creditsResult = ValidationUtil.validateCredits(creditsField.getText());
                if (!creditsResult.isValid()) {
                    showAlert("Validation Error", creditsResult.getErrorMessage());
                    return;
                }
                
                // Validate capacity
                ValidationResult capacityResult = ValidationUtil.validateCapacity(capacityField.getText());
                if (!capacityResult.isValid()) {
                    showAlert("Validation Error", capacityResult.getErrorMessage());
                    return;
                }

                try {
                    String code = codeResult.getStringValue();
                    String title = titleResult.getStringValue();
                    String description = descriptionField.getText().trim();
                    int credits = creditsResult.getIntValue();
                    int capacity = capacityResult.getIntValue();
                    
                    // Extract department ID
                    int departmentId = Integer.parseInt(departmentCombo.getValue().split(":")[0]);
                    
                    // Extract year level
                    int yearLevel = Integer.parseInt(yearCombo.getValue());
                    
                    // Extract semester ID
                    int semesterId = Integer.parseInt(semesterCombo.getValue().split(":")[0]);

                    courseDao.updateCourse(course.getId(), code, title, description, departmentId, 
                                          instructorId, credits, capacity, semesterId, yearLevel);
                    showSuccessAlert("Success", "Course updated successfully!");
                    refreshTableRows(parentRows);
                } catch (SQLException e) {
                    showAlert("Database Error", "Failed to update course: " + e.getMessage());
                } catch (NumberFormatException e) {
                    showAlert("Error", "Invalid selection format: " + e.getMessage());
                }
            }
        });
    }

    private void deleteCourse(CourseV2 course, VBox parentRows) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Course");
        confirm.setHeaderText("Are you sure you want to delete this course?");
        confirm.setContentText("Course: " + course.getCourseCode() + " - " + course.getTitle());

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    courseDao.deleteCourse(course.getId());
                    refreshTableRows(parentRows);
                } catch (SQLException e) {
                    showAlert("Error", "Failed to delete course: " + e.getMessage());
                }
            }
        });
    }

    private void refreshPage() {
        // Rebuild just the content, not the entire scene
        mainContainer.getChildren().clear();
        
        // Rebuild header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label heading = new Label("Manage Courses");
        heading.setFont(FontLoader.getPoppinsBold(28));
        heading.setTextFill(ColorScheme.DARK_TEXT);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button addCourseBtn = new Button("+ Add Course");
        addCourseBtn.setFont(FontLoader.getPoppinsBold(14));
        addCourseBtn.setTextFill(ColorScheme.WHITE);
        addCourseBtn.setPrefHeight(45);
        addCourseBtn.setPrefWidth(150);
        addCourseBtn.setStyle(ColorScheme.getPrimaryButtonStyle());
        addCourseBtn.setOnMouseEntered(e -> addCourseBtn.setStyle(ColorScheme.getPrimaryButtonHoverStyle()));
        addCourseBtn.setOnMouseExited(e -> addCourseBtn.setStyle(ColorScheme.getPrimaryButtonStyle()));
        addCourseBtn.setOnAction(e -> showAddCourseDialog());
        
        header.getChildren().addAll(heading, spacer, addCourseBtn);
        
        // Rebuild table
        VBox tableContainer = buildTableContainer();
        
        mainContainer.getChildren().addAll(header, tableContainer);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
