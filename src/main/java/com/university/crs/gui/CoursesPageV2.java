package com.university.crs.gui;

import com.university.crs.dao.CourseV2Dao;
import com.university.crs.dao.DepartmentDao;
import com.university.crs.dao.InstructorDao;
import com.university.crs.dao.SemesterDao;
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
 * Enhanced Courses management page using courses_v2 table
 */
public class CoursesPageV2 {

    private final Stage stage;
    private final User user;
    private final CourseV2Dao courseDao = new CourseV2Dao();
    private final DepartmentDao departmentDao = new DepartmentDao();
    private final InstructorDao instructorDao = new InstructorDao();
    private final SemesterDao semesterDao = new SemesterDao();
    private VBox tableRowsContainer;

    public CoursesPageV2(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
    }

    public Node build() {
        VBox page = new VBox(30);
        page.setPadding(new Insets(40, 50, 40, 50));
        page.setStyle("-fx-background-color: " + ColorScheme.BACKGROUND_HEX + ";");

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label heading = new Label("Manage Courses");
        heading.setFont(FontLoader.getPoppinsBold(28));
        heading.setTextFill(ColorScheme.DARK_TEXT);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button addCourseBtn = new Button("+ Add Course");
        addCourseBtn.setFont(FontLoader.getPoppinsBold(14));
        addCourseBtn.setTextFill(Color.WHITE);
        addCourseBtn.setPrefHeight(45);
        addCourseBtn.setPrefWidth(170);
        addCourseBtn.setStyle(StyleConstants.buttonPrimary());
        addCourseBtn.setOnMouseEntered(e -> addCourseBtn.setStyle(StyleConstants.buttonPrimaryHover()));
        addCourseBtn.setOnMouseExited(e -> addCourseBtn.setStyle(StyleConstants.buttonPrimary()));
        addCourseBtn.setOnAction(e -> showAddCourseDialog());
        
        header.getChildren().addAll(heading, spacer, addCourseBtn);

        // Table container
        VBox tableContainer = buildTableContainer();

        page.getChildren().addAll(header, tableContainer);
        
        ScrollPane scrollPane = new ScrollPane(page);
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
        Label col3 = createHeaderLabel("Department", 150);
        Label col4 = createHeaderLabel("Instructor", 150);
        Label col5 = createHeaderLabel("Semester", 120);
        Label col6 = createHeaderLabel("Credits", 70);
        Label col7 = createHeaderLabel("Capacity", 80);
        Label col8 = createHeaderLabel("Actions", 100);
        
        headerRow.getChildren().addAll(col1, col2, col3, col4, col5, col6, col7, col8);

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
            List<CourseV2> courses = courseDao.getAllCourses();
            if (courses.isEmpty()) {
                Label emptyLabel = new Label("No courses found. Click 'Add Course' to create one.");
                emptyLabel.setFont(FontLoader.getInter(14));
                emptyLabel.setTextFill(Color.BLACK);
                emptyLabel.setPadding(new Insets(40, 0, 40, 0));
                tableRowsContainer.getChildren().add(emptyLabel);
            } else {
                for (CourseV2 course : courses) {
                    tableRowsContainer.getChildren().add(createTableRow(course));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading courses: " + e.getMessage());
        }
    }

    private HBox createTableRow(CourseV2 course) {
        HBox row = new HBox();
        row.setSpacing(15);
        row.setPadding(new Insets(15, 0, 15, 0));
        row.setStyle("-fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0;");
        
        Label col1 = createCellLabel(course.getCourseCode(), 100);
        Label col2 = createCellLabel(course.getTitle(), 200);
        Label col3 = createCellLabel(course.getDepartmentName(), 150);
        Label col4 = createCellLabel(course.getInstructorName() != null ? course.getInstructorName() : "TBA", 150);
        Label col5 = createCellLabel(course.getSemesterName(), 120);
        Label col6 = createCellLabel(String.valueOf(course.getCredits()), 70);
        Label col7 = createCellLabel(String.valueOf(course.getCapacity()), 80);
        
        // Actions
        HBox actions = new HBox(10);
        actions.setPrefWidth(100);
        actions.setAlignment(Pos.CENTER_LEFT);
        
        Button editBtn = new Button("✏️");
        editBtn.setFont(FontLoader.getInter(16));
        editBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        editBtn.setOnAction(e -> showEditCourseDialog(course));
        
        Button deleteBtn = new Button("🗑️");
        deleteBtn.setFont(FontLoader.getInter(16));
        deleteBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        deleteBtn.setOnAction(e -> deleteCourse(course));
        
        actions.getChildren().addAll(editBtn, deleteBtn);
        
        row.getChildren().addAll(col1, col2, col3, col4, col5, col6, col7, actions);
        
        return row;
    }

    private Label createCellLabel(String text, double width) {
        Label label = new Label(text);
        label.setFont(FontLoader.getInter(13));
        label.setTextFill(Color.BLACK);
        label.setPrefWidth(width);
        label.setWrapText(true);
        return label;
    }

    private void showAddCourseDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Course");
        dialog.setHeaderText("Create a new course");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        TextField codeField = new TextField();
        
        TextField titleField = new TextField();
        
        TextArea descField = new TextArea();
        descField.setPrefRowCount(3);
        
        ComboBox<Department> deptCombo = new ComboBox<>();
        ComboBox<Instructor> instrCombo = new ComboBox<>();
        instrCombo.getItems().add(null); // Allow no instructor
        
        ComboBox<Semester> semesterCombo = new ComboBox<>();
        
        TextField creditsField = new TextField("3");
        TextField capacityField = new TextField("30");
        
        ComboBox<Integer> yearLevelCombo = new ComboBox<>();
        yearLevelCombo.getItems().addAll(1, 2, 3, 4, 5);
        yearLevelCombo.setValue(1);

        // Load data
        try {
            deptCombo.getItems().addAll(departmentDao.getAllDepartments());
            List<Instructor> instructors = instructorDao.getAllInstructors();
            instrCombo.getItems().addAll(instructors);
            semesterCombo.getItems().addAll(semesterDao.getAllSemesters());
        } catch (SQLException e) {
            showAlert("Error", "Failed to load data: " + e.getMessage());
        }

        grid.add(new Label("Course Code:"), 0, 0);
        grid.add(codeField, 1, 0);
        grid.add(new Label("Title:"), 0, 1);
        grid.add(titleField, 1, 1);
        grid.add(new Label("Description:"), 0, 2);
        grid.add(descField, 1, 2);
        grid.add(new Label("Department:"), 0, 3);
        grid.add(deptCombo, 1, 3);
        grid.add(new Label("Instructor:"), 0, 4);
        grid.add(instrCombo, 1, 4);
        grid.add(new Label("Semester:"), 0, 5);
        grid.add(semesterCombo, 1, 5);
        grid.add(new Label("Credits:"), 0, 6);
        grid.add(creditsField, 1, 6);
        grid.add(new Label("Capacity:"), 0, 7);
        grid.add(capacityField, 1, 7);
        grid.add(new Label("Year Level:"), 0, 8);
        grid.add(yearLevelCombo, 1, 8);

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
                
                // Validate description
                ValidationResult descResult = ValidationUtil.validateRequired(descField.getText(), "Course description");
                if (!descResult.isValid()) {
                    showAlert("Validation Error", descResult.getErrorMessage());
                    return;
                }
                
                // Validate department selection
                Department dept = deptCombo.getValue();
                if (dept == null) {
                    showAlert("Validation Error", "Please select a department");
                    return;
                }
                
                // Validate semester selection
                Semester semester = semesterCombo.getValue();
                if (semester == null) {
                    showAlert("Validation Error", "Please select a semester");
                    return;
                }
                
                // Validate credits (with 30 credit limit)
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
                
                // Validate year level
                Integer yearLevel = yearLevelCombo.getValue();
                ValidationResult yearResult = ValidationUtil.validateYearLevel(yearLevel);
                if (!yearResult.isValid()) {
                    showAlert("Validation Error", yearResult.getErrorMessage());
                    return;
                }
                
                try {
                    String code = codeResult.getStringValue();
                    String title = titleResult.getStringValue();
                    String desc = descResult.getStringValue();
                    Instructor instr = instrCombo.getValue();
                    int credits = creditsResult.getIntValue();
                    int capacity = capacityResult.getIntValue();

                    courseDao.addCourse(code, title, desc, dept.getId(),
                                      instr != null ? instr.getId() : null,
                                      credits, capacity, semester.getId(), yearLevel);
                    refreshTableRows();
                    
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Success");
                    success.setContentText("Course created successfully!");
                    success.showAndWait();
                } catch (SQLException e) {
                    showAlert("Database Error", "Failed to create course: " + e.getMessage());
                }
            }
        });
    }

    private void showEditCourseDialog(CourseV2 course) {
        // Similar to add but with pre-filled values
        showAlert("Info", "Edit functionality - similar to add dialog with pre-filled values");
    }

    private void deleteCourse(CourseV2 course) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Course");
        confirm.setHeaderText("Are you sure?");
        confirm.setContentText("Delete course: " + course.getCourseCode() + " - " + course.getTitle());

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    courseDao.deleteCourse(course.getId());
                    refreshTableRows();
                } catch (SQLException e) {
                    showAlert("Error", "Failed to delete course: " + e.getMessage());
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
}
