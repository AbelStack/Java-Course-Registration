package com.university.crs.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Departments management page for Admin.
 * Admin can add, edit departments and assign department heads.
 */
public class DepartmentsPage {

    private final Stage stage;
    private final com.university.crs.model.User user;

    public DepartmentsPage(Stage stage, com.university.crs.model.User user) {
        this.stage = stage;
        this.user = user;
    }

    public Node build() {
        VBox page = new VBox(StyleConstants.SPACING_XL);
        page.setPadding(new Insets(0));
        page.setStyle("-fx-background-color: transparent;");

        // Page header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(header, Priority.ALWAYS);
        
        VBox headerText = new VBox(4);
        Label heading = new Label("Manage Departments");
        heading.setFont(FontLoader.getOutfitSemiBold(24));
        heading.setTextFill(Color.BLACK);
        
        Label subtitle = new Label("Add, edit departments and assign department heads");
        subtitle.setFont(FontLoader.getOutfit(14));
        subtitle.setTextFill(Color.BLACK);
        
        headerText.getChildren().addAll(heading, subtitle);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button addDeptBtn = new Button("+ Add Department");
        addDeptBtn.setFont(FontLoader.getOutfitSemiBold(14));
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
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
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
        
        Label col1 = createHeaderLabel("Department ID", 120);
        Label col2 = createHeaderLabel("Department Name", 300);
        Label col3 = createHeaderLabel("Department Head", 250);
        Label col4 = createHeaderLabel("Actions", 150);
        
        headerRow.getChildren().addAll(col1, col2, col3, col4);

        // Table rows
        VBox rows = new VBox(0);
        loadDepartmentData(rows);

        container.getChildren().addAll(headerRow, rows);
        return container;
    }

    private Label createHeaderLabel(String text, double width) {
        Label label = new Label(text);
        label.setFont(FontLoader.getOutfit(13));
        label.setTextFill(Color.BLACK);
        label.setStyle("-fx-font-weight: 600;");
        label.setPrefWidth(width);
        return label;
    }

    private void loadDepartmentData(VBox rows) {
        // TODO: Load from database
        // For now, show placeholder
        Label emptyLabel = new Label("No departments found. Click 'Add Department' to create one.");
        emptyLabel.setFont(FontLoader.getOutfit(14));
        emptyLabel.setTextFill(Color.BLACK);
        emptyLabel.setPadding(new Insets(40, 0, 40, 0));
        emptyLabel.setAlignment(Pos.CENTER);
        rows.getChildren().add(emptyLabel);
    }

    private void showAddDepartmentDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Department");
        dialog.setHeaderText("Create a new department");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        nameField.setPromptText("e.g., Software Engineering");
        
        ComboBox<String> headCombo = new ComboBox<>();
        headCombo.setPromptText("Select Department Head");
        headCombo.getItems().addAll("-- No Head Assigned --");
        // TODO: Load users with DEPARTMENT_HEAD role from database

        grid.add(new Label("Department Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Department Head:"), 0, 1);
        grid.add(headCombo, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // TODO: Save to database
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Success");
                success.setHeaderText(null);
                success.setContentText("Department created successfully!");
                success.showAndWait();
            }
        });
    }
}
