package com.university.crs.gui;

import com.university.crs.dao.RegistrationDao;
import com.university.crs.model.Registration;
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

/**
 * View Enrollments page - Shows registrations with approval workflow
 */
public class EnrollmentsPageV2 {

    private final Stage stage;
    private final User user;
    private final RegistrationDao registrationDao = new RegistrationDao();
    private VBox tableRowsContainer;
    private ComboBox<String> filterCombo;

    public EnrollmentsPageV2(Stage stage, User user) {
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
        
        Label heading = new Label("View Enrollments");
        heading.setFont(FontLoader.getPoppinsBold(28));
        heading.setTextFill(ColorScheme.DARK_TEXT);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Filter
        filterCombo = new ComboBox<>();
        filterCombo.getItems().addAll("All", "Pending", "Approved", "Rejected", "Dropped");
        filterCombo.setValue("All");
        filterCombo.setOnAction(e -> refreshTableRows());
        
        header.getChildren().addAll(heading, spacer, new Label("Filter:"), filterCombo);

        // Table
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

        // Header
        HBox headerRow = new HBox();
        headerRow.setSpacing(15);
        headerRow.setPadding(new Insets(0, 0, 15, 0));
        headerRow.setStyle("-fx-border-color: " + ColorScheme.SOFT_GRAY_HEX + "; -fx-border-width: 0 0 1 0;");
        
        Label col1 = createHeaderLabel("Student ID", 120);
        Label col2 = createHeaderLabel("Student Name", 180);
        Label col3 = createHeaderLabel("Course", 150);
        Label col4 = createHeaderLabel("Semester", 120);
        Label col5 = createHeaderLabel("Status", 100);
        Label col6 = createHeaderLabel("Requested", 120);
        Label col7 = createHeaderLabel("Actions", 150);
        
        headerRow.getChildren().addAll(col1, col2, col3, col4, col5, col6, col7);

        // Rows
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
            List<Registration> registrations;
            String filter = filterCombo.getValue();
            
            if ("Pending".equals(filter)) {
                registrations = registrationDao.getPendingRegistrations();
            } else {
                registrations = registrationDao.getAllRegistrations();
                if (!"All".equals(filter)) {
                    registrations = registrations.stream()
                        .filter(r -> r.getStatus().equalsIgnoreCase(filter))
                        .toList();
                }
            }
            
            if (registrations.isEmpty()) {
                Label emptyLabel = new Label("No registrations found.");
                emptyLabel.setFont(FontLoader.getInter(14));
                emptyLabel.setPadding(new Insets(40, 0, 40, 0));
                tableRowsContainer.getChildren().add(emptyLabel);
            } else {
                for (Registration reg : registrations) {
                    tableRowsContainer.getChildren().add(createTableRow(reg));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading registrations: " + e.getMessage());
        }
    }

    private HBox createTableRow(Registration reg) {
        HBox row = new HBox();
        row.setSpacing(15);
        row.setPadding(new Insets(15, 0, 15, 0));
        row.setStyle("-fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0;");
        
        Label col1 = createCellLabel(reg.getStudentIdCode(), 120);
        Label col2 = createCellLabel(reg.getStudentName(), 180);
        Label col3 = createCellLabel(reg.getCourseCode() + " - " + reg.getCourseTitle(), 150);
        Label col4 = createCellLabel(reg.getSemesterName(), 120);
        
        Label statusLabel = new Label(reg.getStatus());
        statusLabel.setFont(FontLoader.getInter(12));
        statusLabel.setPrefWidth(100);
        String statusColor = switch (reg.getStatus()) {
            case "APPROVED" -> ColorScheme.SUCCESS_600_HEX;
            case "PENDING" -> ColorScheme.WARNING_500_HEX;
            case "REJECTED" -> ColorScheme.ERROR_600_HEX;
            case "DROPPED" -> ColorScheme.GRAY_500_HEX;
            default -> ColorScheme.GRAY_600_HEX;
        };
        statusLabel.setTextFill(Color.web(statusColor));
        statusLabel.setStyle("-fx-font-weight: 600;");
        
        Label col6 = createCellLabel(reg.getRequestedAt().toLocalDate().toString(), 120);
        
        // Actions
        HBox actions = new HBox(10);
        actions.setPrefWidth(150);
        actions.setAlignment(Pos.CENTER_LEFT);
        
        if ("PENDING".equals(reg.getStatus())) {
            Button approveBtn = new Button("✓");
            approveBtn.setFont(FontLoader.getInter(16));
            approveBtn.setStyle("-fx-background-color: " + ColorScheme.SUCCESS_500_HEX + "; -fx-text-fill: white; -fx-cursor: hand;");
            approveBtn.setTooltip(new Tooltip("Approve"));
            approveBtn.setOnAction(e -> approveRegistration(reg));
            
            Button rejectBtn = new Button("✗");
            rejectBtn.setFont(FontLoader.getInter(16));
            rejectBtn.setStyle("-fx-background-color: " + ColorScheme.ERROR_500_HEX + "; -fx-text-fill: white; -fx-cursor: hand;");
            rejectBtn.setTooltip(new Tooltip("Reject"));
            rejectBtn.setOnAction(e -> rejectRegistration(reg));
            
            actions.getChildren().addAll(approveBtn, rejectBtn);
        }
        
        Button deleteBtn = new Button("🗑️");
        deleteBtn.setFont(FontLoader.getInter(16));
        deleteBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        deleteBtn.setOnAction(e -> deleteRegistration(reg));
        
        actions.getChildren().add(deleteBtn);
        
        row.getChildren().addAll(col1, col2, col3, col4, statusLabel, col6, actions);
        
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

    private void approveRegistration(Registration reg) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Approve Registration");
        dialog.setHeaderText("Approve registration for " + reg.getStudentName());
        dialog.setContentText("Notes (optional):");

        dialog.showAndWait().ifPresent(notes -> {
            try {
                registrationDao.approveRegistration(reg.getId(), user.getId(), notes);
                refreshTableRows();
                
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Success");
                success.setContentText("Registration approved!");
                success.showAndWait();
            } catch (SQLException e) {
                showAlert("Error", "Failed to approve: " + e.getMessage());
            }
        });
    }

    private void rejectRegistration(Registration reg) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reject Registration");
        dialog.setHeaderText("Reject registration for " + reg.getStudentName());
        dialog.setContentText("Reason (required):");

        dialog.showAndWait().ifPresent(reason -> {
            if (reason.trim().isEmpty()) {
                showAlert("Error", "Reason is required for rejection.");
                return;
            }
            
            try {
                registrationDao.rejectRegistration(reg.getId(), user.getId(), reason);
                refreshTableRows();
                
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Success");
                success.setContentText("Registration rejected!");
                success.showAndWait();
            } catch (SQLException e) {
                showAlert("Error", "Failed to reject: " + e.getMessage());
            }
        });
    }

    private void deleteRegistration(Registration reg) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Registration");
        confirm.setHeaderText("Are you sure?");
        confirm.setContentText("Delete registration for " + reg.getStudentName() + " - " + reg.getCourseCode());

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    registrationDao.deleteRegistration(reg.getId());
                    refreshTableRows();
                } catch (SQLException e) {
                    showAlert("Error", "Failed to delete: " + e.getMessage());
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
