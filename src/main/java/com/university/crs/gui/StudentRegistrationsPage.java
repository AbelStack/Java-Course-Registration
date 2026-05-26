package com.university.crs.gui;

import com.university.crs.dao.RegistrationDao;
import com.university.crs.dao.StudentV2Dao;
import com.university.crs.model.Registration;
import com.university.crs.model.StudentV2;
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
 * Student Registrations Page - View registration requests and their status
 */
public class StudentRegistrationsPage {

    private final Stage stage;
    private final User user;
    private final RegistrationDao registrationDao = new RegistrationDao();
    private final StudentV2Dao studentDao = new StudentV2Dao();
    
    private VBox registrationsContainer;
    private StudentV2 currentStudent;
    private ComboBox<String> statusFilter;

    public StudentRegistrationsPage(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
    }

    public Node build() {
        VBox page = new VBox(30);
        page.setPadding(new Insets(40, 50, 40, 50));
        page.setStyle("-fx-background-color: " + ColorScheme.BACKGROUND_HEX + ";");

        // Load current student
        try {
            currentStudent = studentDao.getStudentByStudentId(user.getUsername());
        } catch (SQLException e) {
            showAlert("Error", "Failed to load student information: " + e.getMessage());
            return page;
        }

        // Header
        VBox header = buildHeader();

        // Stats row
        HBox statsRow = buildStatsRow();

        // Filter
        HBox filterRow = buildFilterRow();

        // Registrations list
        registrationsContainer = new VBox(15);
        refreshRegistrations();

        page.getChildren().addAll(header, statsRow, filterRow, registrationsContainer);
        
        ScrollPane scrollPane = new ScrollPane(page);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + ColorScheme.BACKGROUND_HEX + "; -fx-background-color: " + ColorScheme.BACKGROUND_HEX + ";");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        return scrollPane;
    }

    private VBox buildHeader() {
        VBox header = new VBox(8);
        
        Label heading = new Label("My Registrations");
        heading.setFont(FontLoader.getPoppinsBold(28));
        heading.setTextFill(ColorScheme.DARK_TEXT);
        
        Label subtitle = new Label("Track your course registration requests");
        subtitle.setFont(FontLoader.getOutfit(14));
        subtitle.setTextFill(ColorScheme.MEDIUM_TEXT);
        
        header.getChildren().addAll(heading, subtitle);
        return header;
    }

    private HBox buildStatsRow() {
        HBox statsRow = new HBox(20);
        
        try {
            int pendingCount = registrationDao.getPendingRegistrationsCount(currentStudent.getId());
            int approvedCount = registrationDao.getApprovedRegistrationsCount(currentStudent.getId());
            int rejectedCount = registrationDao.getRejectedRegistrationsCount(currentStudent.getId());
            
            statsRow.getChildren().addAll(
                createMiniStatCard("⏳", "Pending", String.valueOf(pendingCount), ColorScheme.WARNING_500),
                createMiniStatCard("✅", "Approved", String.valueOf(approvedCount), ColorScheme.SUCCESS_500),
                createMiniStatCard("❌", "Rejected", String.valueOf(rejectedCount), ColorScheme.ERROR_500)
            );
        } catch (SQLException e) {
            System.err.println("Error loading registration stats: " + e.getMessage());
        }
        
        return statsRow;
    }

    private VBox createMiniStatCard(String icon, String label, String value, Color accentColor) {
        VBox card = new VBox(8);
        card.setStyle(StyleConstants.card());
        card.setPadding(new Insets(20));
        card.setPrefWidth(200);
        card.setAlignment(Pos.CENTER);
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(FontLoader.getOutfitBold(24));
        
        Label valueLabel = new Label(value);
        valueLabel.setFont(FontLoader.getPoppinsBold(24));
        valueLabel.setTextFill(accentColor);
        
        Label textLabel = new Label(label);
        textLabel.setFont(FontLoader.getOutfit(13));
        textLabel.setTextFill(ColorScheme.MEDIUM_TEXT);
        
        card.getChildren().addAll(iconLabel, valueLabel, textLabel);
        
        return card;
    }

    private HBox buildFilterRow() {
        HBox filterRow = new HBox(15);
        filterRow.setAlignment(Pos.CENTER_LEFT);
        
        Label filterLabel = new Label("Filter by Status:");
        filterLabel.setFont(FontLoader.getOutfitMedium(14));
        filterLabel.setTextFill(ColorScheme.DARK_TEXT);
        
        statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All Status", "Pending", "Approved", "Rejected");
        statusFilter.setValue("All Status");
        statusFilter.setPrefWidth(180);
        statusFilter.setOnAction(e -> refreshRegistrations());
        
        filterRow.getChildren().addAll(filterLabel, statusFilter);
        
        return filterRow;
    }

    private void refreshRegistrations() {
        registrationsContainer.getChildren().clear();
        
        try {
            List<Registration> registrations = registrationDao.getRegistrationsByStudent(currentStudent.getId());
            
            // Apply filter
            String selectedStatus = statusFilter != null ? statusFilter.getValue() : "All Status";
            
            for (Registration registration : registrations) {
                if (!selectedStatus.equals("All Status") && !registration.getStatus().equalsIgnoreCase(selectedStatus)) {
                    continue;
                }
                registrationsContainer.getChildren().add(createRegistrationCard(registration));
            }
            
            if (registrations.isEmpty() || registrationsContainer.getChildren().isEmpty()) {
                Label emptyLabel = new Label("No registrations found.");
                emptyLabel.setFont(FontLoader.getOutfit(14));
                emptyLabel.setTextFill(ColorScheme.MEDIUM_TEXT);
                emptyLabel.setPadding(new Insets(40));
                registrationsContainer.getChildren().add(emptyLabel);
            }
            
        } catch (SQLException e) {
            showAlert("Error", "Failed to load registrations: " + e.getMessage());
        }
    }

    private HBox createRegistrationCard(Registration registration) {
        HBox card = new HBox(20);
        card.setStyle(StyleConstants.card());
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER_LEFT);
        
        // Status icon
        VBox statusBox = new VBox(4);
        statusBox.setAlignment(Pos.CENTER);
        statusBox.setPrefWidth(80);
        
        String statusIcon = "";
        Color statusColor = ColorScheme.GRAY_500;
        String statusBgColor = ColorScheme.GRAY_50_HEX;
        
        switch (registration.getStatus().toUpperCase()) {
            case "PENDING":
                statusIcon = "⏳";
                statusColor = ColorScheme.WARNING_600;
                statusBgColor = ColorScheme.WARNING_50_HEX;
                break;
            case "APPROVED":
                statusIcon = "✅";
                statusColor = ColorScheme.SUCCESS_600;
                statusBgColor = ColorScheme.SUCCESS_50_HEX;
                break;
            case "REJECTED":
                statusIcon = "❌";
                statusColor = ColorScheme.ERROR_600;
                statusBgColor = ColorScheme.ERROR_50_HEX;
                break;
        }
        
        Label icon = new Label(statusIcon);
        icon.setFont(FontLoader.getOutfitBold(28));
        
        Label statusLabel = new Label(registration.getStatus());
        statusLabel.setFont(FontLoader.getOutfitSemiBold(11));
        statusLabel.setTextFill(statusColor);
        statusLabel.setStyle(
            "-fx-background-color: " + statusBgColor + "; " +
            "-fx-padding: 4 10; " +
            "-fx-background-radius: 10;"
        );
        
        statusBox.getChildren().addAll(icon, statusLabel);
        
        // Course info
        VBox courseInfo = new VBox(6);
        HBox.setHgrow(courseInfo, Priority.ALWAYS);
        
        Label courseCode = new Label(registration.getCourseCode());
        courseCode.setFont(FontLoader.getOutfitSemiBold(14));
        courseCode.setTextFill(ColorScheme.PRIMARY_600);
        
        Label courseTitle = new Label(registration.getCourseTitle());
        courseTitle.setFont(FontLoader.getPoppinsBold(16));
        courseTitle.setTextFill(ColorScheme.DARK_TEXT);
        
        Label requestDate = new Label("Requested: " + registration.getRequestedAt().toLocalDate());
        requestDate.setFont(FontLoader.getOutfit(12));
        requestDate.setTextFill(ColorScheme.MEDIUM_TEXT);
        
        courseInfo.getChildren().addAll(courseCode, courseTitle, requestDate);
        
        // Additional info
        VBox additionalInfo = new VBox(6);
        additionalInfo.setAlignment(Pos.TOP_RIGHT);
        additionalInfo.setPrefWidth(200);
        
        Label creditsLabel = new Label(registration.getCredits() + " Credits");
        creditsLabel.setFont(FontLoader.getOutfitSemiBold(13));
        creditsLabel.setTextFill(ColorScheme.DARK_TEXT);
        
        if (registration.getProcessedAt() != null) {
            Label processedLabel = new Label("Processed: " + registration.getProcessedAt().toLocalDate());
            processedLabel.setFont(FontLoader.getOutfit(11));
            processedLabel.setTextFill(ColorScheme.MEDIUM_TEXT);
            additionalInfo.getChildren().add(processedLabel);
        }
        
        if (registration.getNotes() != null && !registration.getNotes().isEmpty()) {
            Label notesLabel = new Label("Note: " + registration.getNotes());
            notesLabel.setFont(FontLoader.getOutfit(11));
            notesLabel.setTextFill(ColorScheme.MEDIUM_TEXT);
            notesLabel.setWrapText(true);
            notesLabel.setMaxWidth(190);
            additionalInfo.getChildren().add(notesLabel);
        }
        
        additionalInfo.getChildren().add(0, creditsLabel);
        
        card.getChildren().addAll(statusBox, courseInfo, additionalInfo);
        
        return card;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
