package com.university.crs.gui;

import com.university.crs.dao.InstructorDao;
import com.university.crs.model.Instructor;
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

/**
 * Instructors management page — clean table with Add / Edit / Delete.
 */
public class InstructorsPage {

    private final Stage stage;
    private final User  user;
    private final InstructorDao instructorDao = new InstructorDao();
    private VBox tableRowsContainer;

    public InstructorsPage(Stage stage, User user) {
        this.stage = stage;
        this.user  = user;
    }

    public Node build() {
        VBox page = new VBox(30);
        page.setPadding(new Insets(40, 50, 40, 50));
        page.setStyle("-fx-background-color: " + ColorScheme.BACKGROUND_HEX + ";");

        // Header with title and Add Instructor button
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label heading = new Label("Manage Instructors");
        heading.setFont(FontLoader.getPoppinsBold(28));
        heading.setTextFill(ColorScheme.DARK_TEXT);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button addInstructorBtn = new Button("+ Add Instructor");
        addInstructorBtn.setFont(FontLoader.getPoppinsBold(14));
        addInstructorBtn.setTextFill(ColorScheme.WHITE);
        addInstructorBtn.setPrefHeight(45);
        addInstructorBtn.setPrefWidth(170);
        addInstructorBtn.setStyle(ColorScheme.getPrimaryButtonStyle());
        addInstructorBtn.setOnMouseEntered(e -> addInstructorBtn.setStyle(ColorScheme.getPrimaryButtonHoverStyle()));
        addInstructorBtn.setOnMouseExited(e -> addInstructorBtn.setStyle(ColorScheme.getPrimaryButtonStyle()));
        addInstructorBtn.setOnAction(e -> showAddInstructorDialog());
        
        header.getChildren().addAll(heading, spacer, addInstructorBtn);

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
        headerRow.setSpacing(20);
        headerRow.setPadding(new Insets(0, 0, 15, 0));
        headerRow.setStyle("-fx-border-color: " + ColorScheme.SOFT_GRAY_HEX + "; -fx-border-width: 0 0 1 0;");
        
        Label col1 = createHeaderLabel("ID", 80);
        Label col2 = createHeaderLabel("Name", 250);
        Label col3 = createHeaderLabel("Email", 280);
        Label col4 = createHeaderLabel("Department", 200);
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
            List<Instructor> instructors = instructorDao.getAllInstructors();
            for (Instructor instructor : instructors) {
                tableRowsContainer.getChildren().add(createTableRow(instructor));
            }
        } catch (SQLException e) {
            System.err.println("Error loading instructors: " + e.getMessage());
        }
    }

    private HBox createTableRow(Instructor instructor) {
        HBox row = new HBox();
        row.setSpacing(20);
        row.setPadding(new Insets(15, 0, 15, 0));
        row.setStyle("-fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0;");
        
        Label col1 = createCellLabel(String.valueOf(instructor.getId()), 80);
        Label col2 = createCellLabel(instructor.getName(), 250);
        Label col3 = createCellLabel(instructor.getEmail(), 280);
        Label col4 = createCellLabel(instructor.getDepartment() != null ? instructor.getDepartment() : "N/A", 200);
        
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
        editBtn.setOnAction(e -> showEditInstructorDialog(instructor));
        
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
        deleteBtn.setOnAction(e -> deleteInstructor(instructor));
        
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

    private void showAddInstructorDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New Instructor");
        dialog.setHeaderText("Enter instructor details");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        
        TextField emailField = new TextField();
        
        TextField departmentField = new TextField();

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Department:"), 0, 2);
        grid.add(departmentField, 1, 2);

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
                
                // Department is optional but should not be empty if provided
                String department = departmentField.getText().trim();
                if (department.isEmpty()) {
                    department = null;
                }

                try {
                    String name = nameResult.getStringValue();
                    String email = emailResult.getStringValue();

                    instructorDao.addInstructor(name, email, department);
                    showSuccessAlert("Success", "Instructor added successfully!");
                    refreshTableRows();
                } catch (SQLException e) {
                    showAlert("Database Error", "Failed to add instructor: " + e.getMessage());
                }
            }
        });
    }

    private void showEditInstructorDialog(Instructor instructor) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Instructor");
        dialog.setHeaderText("Update instructor details");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField(instructor.getName());
        TextField emailField = new TextField(instructor.getEmail());
        TextField departmentField = new TextField(instructor.getDepartment() != null ? instructor.getDepartment() : "");

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Department:"), 0, 2);
        grid.add(departmentField, 1, 2);

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
                
                // Department is optional but should not be empty if provided
                String department = departmentField.getText().trim();
                if (department.isEmpty()) {
                    department = null;
                }

                try {
                    String name = nameResult.getStringValue();
                    String email = emailResult.getStringValue();

                    instructorDao.updateInstructor(instructor.getId(), name, email, department);
                    showSuccessAlert("Success", "Instructor updated successfully!");
                    refreshTableRows();
                } catch (SQLException e) {
                    showAlert("Database Error", "Failed to update instructor: " + e.getMessage());
                }
            }
        });
    }

    private void deleteInstructor(Instructor instructor) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Instructor");
        confirm.setHeaderText("Are you sure you want to delete this instructor?");
        confirm.setContentText("Instructor: " + instructor.getName() + " (" + instructor.getEmail() + ")\n\n" +
                               "Note: Courses assigned to this instructor will have no instructor.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    instructorDao.deleteInstructor(instructor.getId());
                    showSuccessAlert("Success", "Instructor deleted successfully!");
                    refreshTableRows();
                } catch (SQLException e) {
                    showAlert("Error", "Failed to delete instructor: " + e.getMessage());
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
    
    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
