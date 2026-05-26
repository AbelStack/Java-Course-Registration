package com.university.crs.gui;

import com.university.crs.dao.DepartmentDao;
import com.university.crs.dao.UserDao;
import com.university.crs.model.Department;
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
 * Departments management page for Admin.
 * Admin can add, edit departments and assign department heads.
 */
public class DepartmentsPage {

    private final Stage stage;
    private final User user;
    private final DepartmentDao departmentDao = new DepartmentDao();
    private final UserDao userDao = new UserDao();
    private VBox tableRowsContainer;

    public DepartmentsPage(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
    }

    public Node build() {
        VBox page = new VBox(StyleConstants.SPACING_XL);
        page.setPadding(new Insets(40, 50, 40, 50));
        page.setStyle("-fx-background-color: " + ColorScheme.BACKGROUND_HEX + ";");

        // Page header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(header, Priority.ALWAYS);
        
        VBox headerText = new VBox(4);
        Label heading = new Label("Manage Departments");
        heading.setFont(FontLoader.getPoppinsBold(28));
        heading.setTextFill(ColorScheme.DARK_TEXT);
        
        Label subtitle = new Label("Add, edit departments and manage department information");
        subtitle.setFont(FontLoader.getOutfit(14));
        subtitle.setTextFill(ColorScheme.MEDIUM_TEXT);
        
        headerText.getChildren().addAll(heading, subtitle);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button addDeptBtn = new Button("+ Add Department");
        addDeptBtn.setFont(FontLoader.getPoppinsBold(14));
        addDeptBtn.setTextFill(Color.WHITE);
        addDeptBtn.setPrefHeight(45);
        addDeptBtn.setPrefWidth(180);
        addDeptBtn.setStyle(StyleConstants.buttonPrimary());
        addDeptBtn.setOnMouseEntered(e -> addDeptBtn.setStyle(StyleConstants.buttonPrimaryHover()));
        addDeptBtn.setOnMouseExited(e -> addDeptBtn.setStyle(StyleConstants.buttonPrimary()));
        addDeptBtn.setOnAction(e -> showAddDepartmentDialog());
        
        header.getChildren().addAll(headerText, spacer, addDeptBtn);

        // Departments table
        VBox tableContainer = buildDepartmentsTable();

        page.getChildren().addAll(header, tableContainer);
        
        ScrollPane scrollPane = new ScrollPane(page);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + ColorScheme.BACKGROUND_HEX + "; -fx-background-color: " + ColorScheme.BACKGROUND_HEX + ";");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        return scrollPane;
    }

    private VBox buildDepartmentsTable() {
        VBox container = new VBox();
        container.setStyle(StyleConstants.card());
        container.setPadding(new Insets(StyleConstants.SPACING_XL));

        // Table header
        HBox headerRow = new HBox();
        headerRow.setSpacing(20);
        headerRow.setPadding(new Insets(0, 0, 15, 0));
        headerRow.setStyle("-fx-border-color: " + ColorScheme.GRAY_200_HEX + "; -fx-border-width: 0 0 1 0;");
        
        Label col1 = createHeaderLabel("Code", 120);
        Label col2 = createHeaderLabel("Department Name", 400);
        Label col3 = createHeaderLabel("Actions", 150);
        
        headerRow.getChildren().addAll(col1, col2, col3);

        // Table rows
        tableRowsContainer = new VBox(0);
        refreshTableRows();

        container.getChildren().addAll(headerRow, tableRowsContainer);
        return container;
    }

    private Label createHeaderLabel(String text, double width) {
        Label label = new Label(text);
        label.setFont(FontLoader.getInter(13));
        label.setTextFill(ColorScheme.DARK_TEXT);
        label.setStyle("-fx-font-weight: 600;");
        label.setPrefWidth(width);
        return label;
    }

    private void refreshTableRows() {
        tableRowsContainer.getChildren().clear();
        try {
            List<Department> departments = departmentDao.getAllDepartments();
            if (departments.isEmpty()) {
                Label emptyLabel = new Label("No departments found. Click 'Add Department' to create one.");
                emptyLabel.setFont(FontLoader.getInter(14));
                emptyLabel.setTextFill(ColorScheme.MEDIUM_TEXT);
                emptyLabel.setPadding(new Insets(40, 0, 40, 0));
                emptyLabel.setAlignment(Pos.CENTER);
                tableRowsContainer.getChildren().add(emptyLabel);
            } else {
                for (Department dept : departments) {
                    tableRowsContainer.getChildren().add(createTableRow(dept));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading departments: " + e.getMessage());
            showAlert("Error", "Failed to load departments: " + e.getMessage());
        }
    }

    private HBox createTableRow(Department dept) {
        HBox row = new HBox();
        row.setSpacing(20);
        row.setPadding(new Insets(15, 0, 15, 0));
        row.setStyle("-fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0;");
        
        Label col1 = createCellLabel(dept.getCode(), 120);
        Label col2 = createCellLabel(dept.getName(), 400);
        
        // Actions
        HBox actions = new HBox(15);
        actions.setPrefWidth(150);
        actions.setAlignment(Pos.CENTER_LEFT);
        
        // Edit button
        Button editBtn = new Button("✏️");
        editBtn.setFont(FontLoader.getOutfit(18));
        editBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 5;");
        editBtn.setOnMouseEntered(e -> editBtn.setStyle(
            "-fx-background-color: rgba(74, 144, 226, 0.1); -fx-cursor: hand; -fx-padding: 5; -fx-background-radius: 5;"));
        editBtn.setOnMouseExited(e -> editBtn.setStyle(
            "-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 5;"));
        editBtn.setOnAction(e -> showEditDepartmentDialog(dept));
        
        // Delete button
        Button deleteBtn = new Button("🗑️");
        deleteBtn.setFont(FontLoader.getOutfit(18));
        deleteBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 5;");
        deleteBtn.setOnMouseEntered(e -> deleteBtn.setStyle(
            "-fx-background-color: rgba(234, 84, 85, 0.1); -fx-cursor: hand; -fx-padding: 5; -fx-background-radius: 5;"));
        deleteBtn.setOnMouseExited(e -> deleteBtn.setStyle(
            "-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 5;"));
        deleteBtn.setOnAction(e -> deleteDepartment(dept));
        
        actions.getChildren().addAll(editBtn, deleteBtn);
        
        row.getChildren().addAll(col1, col2, actions);
        
        // Hover effect
        row.setOnMouseEntered(e -> row.setStyle(
            "-fx-background-color: #f9fafb; -fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0; -fx-cursor: hand;"));
        row.setOnMouseExited(e -> row.setStyle(
            "-fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0;"));
        
        return row;
    }

    private Label createCellLabel(String text, double width) {
        Label label = new Label(text);
        label.setFont(FontLoader.getInter(14));
        label.setTextFill(ColorScheme.DARK_TEXT);
        label.setPrefWidth(width);
        return label;
    }

    private void showAddDepartmentDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Department");
        dialog.setHeaderText("Create a new department");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        TextField codeField = new TextField();
        
        TextField nameField = new TextField();

        grid.add(new Label("Department Code:"), 0, 0);
        grid.add(codeField, 1, 0);
        grid.add(new Label("Department Name:"), 0, 1);
        grid.add(nameField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Validate department code
                ValidationResult codeResult = ValidationUtil.validateDepartmentCode(codeField.getText());
                if (!codeResult.isValid()) {
                    showAlert("Validation Error", codeResult.getErrorMessage());
                    return;
                }
                
                // Validate department name
                ValidationResult nameResult = ValidationUtil.validateDepartmentName(nameField.getText());
                if (!nameResult.isValid()) {
                    showAlert("Validation Error", nameResult.getErrorMessage());
                    return;
                }
                
                String code = codeResult.getStringValue();
                String name = nameResult.getStringValue();
                
                try {
                    if (departmentDao.codeExists(code)) {
                        showAlert("Error", "Department code already exists.");
                        return;
                    }
                    
                    departmentDao.addDepartment(code, name);
                    refreshTableRows();
                    
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Success");
                    success.setHeaderText(null);
                    success.setContentText("Department created successfully!");
                    success.showAndWait();
                } catch (SQLException e) {
                    showAlert("Database Error", "Failed to create department: " + e.getMessage());
                }
            }
        });
    }

    private void showEditDepartmentDialog(Department dept) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Department");
        dialog.setHeaderText("Update department information");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        TextField codeField = new TextField(dept.getCode());
        TextField nameField = new TextField(dept.getName());

        grid.add(new Label("Department Code:"), 0, 0);
        grid.add(codeField, 1, 0);
        grid.add(new Label("Department Name:"), 0, 1);
        grid.add(nameField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Validate department code
                ValidationResult codeResult = ValidationUtil.validateDepartmentCode(codeField.getText());
                if (!codeResult.isValid()) {
                    showAlert("Validation Error", codeResult.getErrorMessage());
                    return;
                }
                
                // Validate department name
                ValidationResult nameResult = ValidationUtil.validateDepartmentName(nameField.getText());
                if (!nameResult.isValid()) {
                    showAlert("Validation Error", nameResult.getErrorMessage());
                    return;
                }
                
                String code = codeResult.getStringValue();
                String name = nameResult.getStringValue();
                
                try {
                    departmentDao.updateDepartment(dept.getId(), code, name);
                    refreshTableRows();
                    
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Success");
                    success.setHeaderText(null);
                    success.setContentText("Department updated successfully!");
                    success.showAndWait();
                } catch (SQLException e) {
                    showAlert("Database Error", "Failed to update department: " + e.getMessage());
                }
            }
        });
    }

    private void deleteDepartment(Department dept) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Department");
        confirm.setHeaderText("Are you sure you want to delete this department?");
        confirm.setContentText("Department: " + dept.getName() + " (" + dept.getCode() + ")");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    departmentDao.deleteDepartment(dept.getId());
                    refreshTableRows();
                } catch (SQLException e) {
                    showAlert("Error", "Failed to delete department: " + e.getMessage());
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
