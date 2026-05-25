package com.university.crs.gui;

import com.university.crs.dao.UserDao;
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
 * Student approvals page - admin can approve or reject pending student registrations.
 * Updated to match client design system.
 */
public class StudentApprovalsPage {

    private final Stage stage;
    private final User currentUser;
    private final UserDao userDao = new UserDao();
    private VBox pendingList;

    public StudentApprovalsPage(Stage stage, User currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
    }

    public Node build() {
        VBox page = new VBox(StyleConstants.SPACING_XL);
        page.setPadding(new Insets(0));
        page.setStyle("-fx-background-color: transparent;");

        // Header
        VBox header = new VBox(4);
        Label heading = new Label("Student Approvals");
        heading.setFont(FontLoader.getOutfitSemiBold(24));
        heading.setTextFill(ColorScheme.GRAY_900);
        
        Label subtitle = new Label("Review and approve student registration requests");
        subtitle.setFont(FontLoader.getOutfit(14));
        subtitle.setTextFill(Color.BLACK);
        
        header.getChildren().addAll(heading, subtitle);

        // Info banner
        HBox infoBanner = new HBox(StyleConstants.SPACING_MD);
        infoBanner.setPadding(new Insets(StyleConstants.SPACING_MD, StyleConstants.SPACING_LG, StyleConstants.SPACING_MD, StyleConstants.SPACING_LG));
        infoBanner.setAlignment(Pos.CENTER_LEFT);
        infoBanner.setStyle(String.format(
            "-fx-background-color: %s; " +
            "-fx-border-color: %s; " +
            "-fx-border-radius: %.0fpx; " +
            "-fx-background-radius: %.0fpx;",
            ColorScheme.BRAND_50_HEX,
            ColorScheme.BRAND_300_HEX,
            StyleConstants.RADIUS_MD,
            StyleConstants.RADIUS_MD
        ));
        
        Label infoIcon = new Label("ℹ️");
        infoIcon.setFont(FontLoader.getOutfit(20));
        
        Label infoText = new Label("Students cannot access the system until their account is approved.");
        infoText.setFont(FontLoader.getOutfit(14));
        infoText.setTextFill(ColorScheme.BRAND_600);
        infoText.setWrapText(true);
        HBox.setHgrow(infoText, Priority.ALWAYS);
        
        infoBanner.getChildren().addAll(infoIcon, infoText);

        // Pending students container
        VBox container = new VBox(StyleConstants.SPACING_LG);
        container.setStyle(StyleConstants.card());
        container.setPadding(new Insets(StyleConstants.SPACING_XL));

        Label sectionTitle = new Label("Pending Approvals");
        sectionTitle.setFont(FontLoader.getOutfitSemiBold(18));
        sectionTitle.setTextFill(ColorScheme.GRAY_900);

        pendingList = new VBox(StyleConstants.SPACING_MD);
        loadPendingStudents();

        container.getChildren().addAll(sectionTitle, pendingList);

        page.getChildren().addAll(header, infoBanner, container);
        
        ScrollPane scrollPane = new ScrollPane(page);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        return scrollPane;
    }

    private void loadPendingStudents() {
        pendingList.getChildren().clear();
        
        try {
            List<User> pending = userDao.getPendingStudents();
            
            if (pending.isEmpty()) {
                // Empty state
                VBox emptyState = new VBox(StyleConstants.SPACING_MD);
                emptyState.setAlignment(Pos.CENTER);
                emptyState.setPadding(new Insets(60));
                
                Label emptyIcon = new Label("✓");
                emptyIcon.setFont(FontLoader.getOutfit(48));
                emptyIcon.setTextFill(ColorScheme.SUCCESS_500);
                
                Label emptyText = new Label("No pending approvals");
                emptyText.setFont(FontLoader.getOutfit(14));
                emptyText.setTextFill(Color.BLACK);
                
                Label emptySubtext = new Label("All student registrations have been reviewed");
                emptySubtext.setFont(FontLoader.getOutfit(12));
                emptySubtext.setTextFill(Color.BLACK);
                
                emptyState.getChildren().addAll(emptyIcon, emptyText, emptySubtext);
                pendingList.getChildren().add(emptyState);
            } else {
                for (User student : pending) {
                    pendingList.getChildren().add(createStudentCard(student));
                }
            }
        } catch (SQLException e) {
            // Error state
            VBox errorState = new VBox(StyleConstants.SPACING_MD);
            errorState.setAlignment(Pos.CENTER);
            errorState.setPadding(new Insets(60));
            
            Label errorIcon = new Label("⚠️");
            errorIcon.setFont(FontLoader.getOutfit(48));
            
            Label errorText = new Label("Error loading pending students");
            errorText.setFont(FontLoader.getOutfit(14));
            errorText.setTextFill(ColorScheme.ERROR_500);
            
            Label errorDetails = new Label(e.getMessage());
            errorDetails.setFont(FontLoader.getOutfit(12));
            errorDetails.setTextFill(ColorScheme.ERROR_500);
            errorDetails.setWrapText(true);
            
            errorState.getChildren().addAll(errorIcon, errorText, errorDetails);
            pendingList.getChildren().add(errorState);
        }
    }

    private HBox createStudentCard(User student) {
        HBox card = new HBox(StyleConstants.SPACING_XL);
        card.setPadding(new Insets(StyleConstants.SPACING_LG));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(String.format(
            "-fx-background-color: %s; " +
            "-fx-border-color: %s; " +
            "-fx-border-radius: %.0fpx; " +
            "-fx-background-radius: %.0fpx;",
            ColorScheme.GRAY_50_HEX,
            ColorScheme.GRAY_200_HEX,
            StyleConstants.RADIUS_MD,
            StyleConstants.RADIUS_MD
        ));

        // Student info section
        VBox infoSection = new VBox(6);
        HBox.setHgrow(infoSection, Priority.ALWAYS);
        
        Label nameLabel = new Label(student.getFullName() != null ? student.getFullName() : "N/A");
        nameLabel.setFont(FontLoader.getOutfitSemiBold(16));
        nameLabel.setTextFill(ColorScheme.GRAY_900);
        
        Label usernameLabel = new Label("Username: " + student.getUsername());
        usernameLabel.setFont(FontLoader.getOutfit(14));
        usernameLabel.setTextFill(Color.BLACK);
        
        Label emailLabel = new Label("Email: " + (student.getEmail() != null ? student.getEmail() : "N/A"));
        emailLabel.setFont(FontLoader.getOutfit(14));
        emailLabel.setTextFill(Color.BLACK);
        
        Label deptLabel = new Label("Department: " + (student.getDepartment() != null ? student.getDepartment() : "N/A"));
        deptLabel.setFont(FontLoader.getOutfit(14));
        deptLabel.setTextFill(Color.BLACK);
        
        infoSection.getChildren().addAll(nameLabel, usernameLabel, emailLabel, deptLabel);

        // Action buttons section
        HBox buttonBox = new HBox(StyleConstants.SPACING_MD);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button approveBtn = new Button("✓ Approve");
        approveBtn.setFont(FontLoader.getOutfitMedium(14));
        approveBtn.setPrefHeight(40);
        approveBtn.setPrefWidth(120);
        approveBtn.setStyle(StyleConstants.buttonSuccess());
        approveBtn.setOnMouseEntered(e -> approveBtn.setStyle(
            StyleConstants.buttonSuccess() + "-fx-background-color: " + ColorScheme.SUCCESS_600_HEX + ";"
        ));
        approveBtn.setOnMouseExited(e -> approveBtn.setStyle(StyleConstants.buttonSuccess()));
        approveBtn.setOnAction(e -> handleApprove(student));
        
        Button rejectBtn = new Button("✗ Reject");
        rejectBtn.setFont(FontLoader.getOutfitMedium(14));
        rejectBtn.setPrefHeight(40);
        rejectBtn.setPrefWidth(120);
        rejectBtn.setStyle(StyleConstants.buttonDanger());
        rejectBtn.setOnMouseEntered(e -> rejectBtn.setStyle(
            StyleConstants.buttonDanger() + "-fx-background-color: " + ColorScheme.ERROR_600_HEX + ";"
        ));
        rejectBtn.setOnMouseExited(e -> rejectBtn.setStyle(StyleConstants.buttonDanger()));
        rejectBtn.setOnAction(e -> handleReject(student));
        
        buttonBox.getChildren().addAll(approveBtn, rejectBtn);

        card.getChildren().addAll(infoSection, buttonBox);
        
        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle(String.format(
            "-fx-background-color: white; " +
            "-fx-border-color: %s; " +
            "-fx-border-radius: %.0fpx; " +
            "-fx-background-radius: %.0fpx; " +
            "-fx-effect: %s;",
            ColorScheme.GRAY_300_HEX,
            StyleConstants.RADIUS_MD,
            StyleConstants.RADIUS_MD,
            StyleConstants.SHADOW_SM
        )));
        card.setOnMouseExited(e -> card.setStyle(String.format(
            "-fx-background-color: %s; " +
            "-fx-border-color: %s; " +
            "-fx-border-radius: %.0fpx; " +
            "-fx-background-radius: %.0fpx;",
            ColorScheme.GRAY_50_HEX,
            ColorScheme.GRAY_200_HEX,
            StyleConstants.RADIUS_MD,
            StyleConstants.RADIUS_MD
        )));
        
        return card;
    }

    private void handleApprove(User student) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Approve Student");
        confirm.setHeaderText("Approve " + student.getFullName() + "?");
        confirm.setContentText("This student will be able to log in and access the system.");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    if (userDao.approveStudent(student.getId())) {
                        showSuccess("Student approved successfully!");
                        loadPendingStudents(); // Refresh list
                    } else {
                        showError("Failed to approve student.");
                    }
                } catch (SQLException e) {
                    showError("Error: " + e.getMessage());
                }
            }
        });
    }

    private void handleReject(User student) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Reject Student");
        confirm.setHeaderText("Reject " + student.getFullName() + "?");
        confirm.setContentText("This will permanently delete their account. This action cannot be undone.");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    if (userDao.rejectStudent(student.getId())) {
                        showSuccess("Student account rejected and deleted.");
                        loadPendingStudents(); // Refresh list
                    } else {
                        showError("Failed to reject student.");
                    }
                } catch (SQLException e) {
                    showError("Error: " + e.getMessage());
                }
            }
        });
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
