package com.university.crs.gui;

import com.university.crs.dao.RegistrationPeriodDao;
import com.university.crs.dao.SemesterDao;
import com.university.crs.model.RegistrationPeriod;
import com.university.crs.model.Semester;
import com.university.crs.util.ValidationUtil;
import com.university.crs.util.ValidationUtil.ValidationResult;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Registration Control page - Manage registration periods
 */
public class RegistrationManagementPageV2 {

    private final RegistrationPeriodDao periodDao = new RegistrationPeriodDao();
    private final SemesterDao semesterDao = new SemesterDao();
    private VBox tableRowsContainer;

    public Node build() {
        VBox page = new VBox(StyleConstants.SPACING_XL);
        page.setPadding(new Insets(40, 50, 40, 50));
        page.setStyle("-fx-background-color: " + ColorScheme.BACKGROUND_HEX + ";");

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(header, Priority.ALWAYS);
        
        VBox headerText = new VBox(4);
        Label heading = new Label("Registration Control");
        heading.setFont(FontLoader.getPoppinsBold(28));
        heading.setTextFill(ColorScheme.DARK_TEXT);
        
        Label subtitle = new Label("Manage registration periods for semesters");
        subtitle.setFont(FontLoader.getOutfit(14));
        subtitle.setTextFill(ColorScheme.MEDIUM_TEXT);
        
        headerText.getChildren().addAll(heading, subtitle);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button addPeriodBtn = new Button("+ Add Registration Period");
        addPeriodBtn.setFont(FontLoader.getPoppinsBold(14));
        addPeriodBtn.setTextFill(Color.WHITE);
        addPeriodBtn.setPrefHeight(45);
        addPeriodBtn.setPrefWidth(220);
        addPeriodBtn.setStyle(StyleConstants.buttonPrimary());
        addPeriodBtn.setOnMouseEntered(e -> addPeriodBtn.setStyle(StyleConstants.buttonPrimaryHover()));
        addPeriodBtn.setOnMouseExited(e -> addPeriodBtn.setStyle(StyleConstants.buttonPrimary()));
        addPeriodBtn.setOnAction(e -> showAddPeriodDialog());
        
        header.getChildren().addAll(headerText, spacer, addPeriodBtn);

        // Table
        VBox tableContainer = buildTable();

        page.getChildren().addAll(header, tableContainer);
        
        ScrollPane scrollPane = new ScrollPane(page);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + ColorScheme.BACKGROUND_HEX + "; -fx-background-color: " + ColorScheme.BACKGROUND_HEX + ";");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        return scrollPane;
    }

    private VBox buildTable() {
        VBox container = new VBox();
        container.setStyle(StyleConstants.card());
        container.setPadding(new Insets(StyleConstants.SPACING_XL));

        // Header
        HBox headerRow = new HBox();
        headerRow.setSpacing(20);
        headerRow.setPadding(new Insets(0, 0, 15, 0));
        headerRow.setStyle("-fx-border-color: " + ColorScheme.GRAY_200_HEX + "; -fx-border-width: 0 0 1 0;");
        
        Label col1 = createHeaderLabel("Period Name", 250);
        Label col2 = createHeaderLabel("Semester", 150);
        Label col3 = createHeaderLabel("Start Date", 150);
        Label col4 = createHeaderLabel("End Date", 150);
        Label col5 = createHeaderLabel("Status", 120);
        Label col6 = createHeaderLabel("Actions", 150);
        
        headerRow.getChildren().addAll(col1, col2, col3, col4, col5, col6);

        // Rows
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
            List<RegistrationPeriod> periods = periodDao.getAllRegistrationPeriods();
            if (periods.isEmpty()) {
                Label emptyLabel = new Label("No registration periods found.");
                emptyLabel.setFont(FontLoader.getInter(14));
                emptyLabel.setTextFill(ColorScheme.MEDIUM_TEXT);
                emptyLabel.setPadding(new Insets(40, 0, 40, 0));
                tableRowsContainer.getChildren().add(emptyLabel);
            } else {
                for (RegistrationPeriod period : periods) {
                    tableRowsContainer.getChildren().add(createTableRow(period));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading periods: " + e.getMessage());
        }
    }

    private HBox createTableRow(RegistrationPeriod period) {
        HBox row = new HBox();
        row.setSpacing(20);
        row.setPadding(new Insets(15, 0, 15, 0));
        row.setStyle("-fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0;");
        
        Label col1 = createCellLabel(period.getPeriodName(), 250);
        Label col2 = createCellLabel(period.getSemesterName(), 150);
        Label col3 = createCellLabel(period.getStartDate().toLocalDate().toString(), 150);
        Label col4 = createCellLabel(period.getEndDate().toLocalDate().toString(), 150);
        
        Label statusLabel = new Label(period.getStatus());
        statusLabel.setFont(FontLoader.getInter(12));
        statusLabel.setPrefWidth(120);
        String statusColor = switch (period.getStatus()) {
            case "Active" -> ColorScheme.SUCCESS_600_HEX;
            case "Upcoming" -> ColorScheme.WARNING_500_HEX;
            case "Ended" -> ColorScheme.GRAY_500_HEX;
            default -> ColorScheme.ERROR_600_HEX;
        };
        statusLabel.setTextFill(Color.web(statusColor));
        statusLabel.setStyle("-fx-font-weight: 600;");
        
        // Actions
        HBox actions = new HBox(10);
        actions.setPrefWidth(150);
        actions.setAlignment(Pos.CENTER_LEFT);
        
        Button toggleBtn = new Button(period.isActive() ? "Deactivate" : "Activate");
        toggleBtn.setFont(FontLoader.getInter(12));
        toggleBtn.setStyle("-fx-background-color: " + (period.isActive() ? ColorScheme.ERROR_500_HEX : ColorScheme.SUCCESS_500_HEX) + "; -fx-text-fill: white; -fx-cursor: hand;");
        toggleBtn.setOnAction(e -> togglePeriod(period));
        
        Button deleteBtn = new Button("🗑️");
        deleteBtn.setFont(FontLoader.getInter(16));
        deleteBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        deleteBtn.setOnAction(e -> deletePeriod(period));
        
        actions.getChildren().addAll(toggleBtn, deleteBtn);
        
        row.getChildren().addAll(col1, col2, col3, col4, statusLabel, actions);
        
        return row;
    }

    private Label createCellLabel(String text, double width) {
        Label label = new Label(text);
        label.setFont(FontLoader.getInter(13));
        label.setTextFill(ColorScheme.DARK_TEXT);
        label.setPrefWidth(width);
        return label;
    }

    private void showAddPeriodDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Registration Period");
        dialog.setHeaderText("Create a new registration period");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        
        ComboBox<Semester> semesterCombo = new ComboBox<>();
        try {
            semesterCombo.getItems().addAll(semesterDao.getAllSemesters());
        } catch (SQLException e) {
            showAlert("Error", "Failed to load semesters: " + e.getMessage());
        }
        
        DatePicker startDatePicker = new DatePicker(LocalDate.now());
        DatePicker endDatePicker = new DatePicker(LocalDate.now().plusMonths(2));
        
        CheckBox activeCheck = new CheckBox("Active");
        activeCheck.setSelected(true);

        grid.add(new Label("Period Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Semester:"), 0, 1);
        grid.add(semesterCombo, 1, 1);
        grid.add(new Label("Start Date:"), 0, 2);
        grid.add(startDatePicker, 1, 2);
        grid.add(new Label("End Date:"), 0, 3);
        grid.add(endDatePicker, 1, 3);
        grid.add(new Label("Status:"), 0, 4);
        grid.add(activeCheck, 1, 4);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Validate period name
                ValidationResult nameResult = ValidationUtil.validateLength(nameField.getText(), "Period name", 5, 100);
                if (!nameResult.isValid()) {
                    showAlert("Validation Error", nameResult.getErrorMessage());
                    return;
                }
                
                // Validate semester selection
                Semester semester = semesterCombo.getValue();
                if (semester == null) {
                    showAlert("Validation Error", "Please select a semester");
                    return;
                }
                
                // Validate dates
                LocalDate startDate = startDatePicker.getValue();
                LocalDate endDate = endDatePicker.getValue();
                
                if (startDate == null) {
                    showAlert("Validation Error", "Please select a start date");
                    return;
                }
                
                if (endDate == null) {
                    showAlert("Validation Error", "Please select an end date");
                    return;
                }
                
                if (endDate.isBefore(startDate)) {
                    showAlert("Validation Error", "End date must be after start date");
                    return;
                }
                
                if (startDate.isBefore(LocalDate.now())) {
                    showAlert("Validation Error", "Start date cannot be in the past");
                    return;
                }

                String name = nameResult.getStringValue();
                boolean isActive = activeCheck.isSelected();

                try {
                    periodDao.addRegistrationPeriod(
                        semester.getId(),
                        name,
                        LocalDateTime.of(startDate, LocalTime.MIN),
                        LocalDateTime.of(endDate, LocalTime.MAX),
                        isActive
                    );
                    refreshTableRows();
                    
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Success");
                    success.setContentText("Registration period created successfully!");
                    success.showAndWait();
                } catch (SQLException e) {
                    showAlert("Database Error", "Failed to create period: " + e.getMessage());
                }
            }
        });
    }

    private void togglePeriod(RegistrationPeriod period) {
        try {
            periodDao.toggleActive(period.getId());
            refreshTableRows();
        } catch (SQLException e) {
            showAlert("Error", "Failed to toggle period: " + e.getMessage());
        }
    }

    private void deletePeriod(RegistrationPeriod period) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Period");
        confirm.setHeaderText("Are you sure?");
        confirm.setContentText("Delete: " + period.getPeriodName());

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    periodDao.deleteRegistrationPeriod(period.getId());
                    refreshTableRows();
                } catch (SQLException e) {
                    showAlert("Error", "Failed to delete period: " + e.getMessage());
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
