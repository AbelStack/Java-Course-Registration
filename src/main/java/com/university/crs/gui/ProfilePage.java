package com.university.crs.gui;

import com.university.crs.dao.UserDao;
import com.university.crs.dao.StudentV2Dao;
import com.university.crs.model.User;
import com.university.crs.model.StudentV2;
import com.university.crs.util.ValidationUtil;
import com.university.crs.util.ValidationUtil.ValidationResult;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.sql.SQLException;

/**
 * Profile page — displays user profile information with edit capability.
 * Works for all three user roles: Admin, Department Head, and Student
 */
public class ProfilePage {

    private final User user;
    private final UserDao userDao = new UserDao();
    private final StudentV2Dao studentDao = new StudentV2Dao();
    private VBox profileCard;
    private VBox infoSection;

    public ProfilePage(User user) {
        this.user = user;
    }

    public Node build() {
        VBox page = new VBox(30);
        page.setPadding(new Insets(40, 50, 40, 50));
        page.setStyle("-fx-background-color: " + ColorScheme.BACKGROUND_HEX + ";");
        page.setAlignment(Pos.TOP_CENTER);

        // Header
        VBox header = new VBox(8);
        header.setAlignment(Pos.CENTER);
        
        Label heading = new Label("My Profile");
        heading.setFont(FontLoader.getPoppinsBold(28));
        heading.setTextFill(ColorScheme.DARK_TEXT);
        
        Label subtitle = new Label(getRoleDisplayName());
        subtitle.setFont(FontLoader.getOutfit(14));
        subtitle.setTextFill(ColorScheme.MEDIUM_TEXT);
        
        header.getChildren().addAll(heading, subtitle);

        // Profile card
        profileCard = buildProfileCard();

        page.getChildren().addAll(header, profileCard);
        
        ScrollPane scrollPane = new ScrollPane(page);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + ColorScheme.BACKGROUND_HEX + "; -fx-background-color: " + ColorScheme.BACKGROUND_HEX + ";");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        return scrollPane;
    }

    private VBox buildProfileCard() {
        VBox card = new VBox(30);
        card.setMaxWidth(700);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(40, 50, 40, 50));
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 12; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 15, 0, 0, 3);"
        );

        // Profile avatar with role badge
        StackPane avatarContainer = createAvatar();

        // Profile information fields
        infoSection = new VBox(15);
        infoSection.setAlignment(Pos.CENTER);
        
        refreshProfileInfo();

        // Action buttons
        HBox actionButtons = new HBox(15);
        actionButtons.setAlignment(Pos.CENTER);
        actionButtons.setPadding(new Insets(10, 0, 0, 0));
        
        Button editBtn = new Button("Edit Profile");
        editBtn.setFont(FontLoader.getOutfitSemiBold(14));
        editBtn.setTextFill(Color.WHITE);
        editBtn.setPrefWidth(180);
        editBtn.setPrefHeight(45);
        editBtn.setStyle(ColorScheme.getPrimaryButtonStyle());
        editBtn.setOnMouseEntered(e -> editBtn.setStyle(ColorScheme.getPrimaryButtonHoverStyle()));
        editBtn.setOnMouseExited(e -> editBtn.setStyle(ColorScheme.getPrimaryButtonStyle()));
        editBtn.setOnAction(e -> showEditProfileDialog());
        
        Button changePasswordBtn = new Button("Change Password");
        changePasswordBtn.setFont(FontLoader.getOutfitSemiBold(14));
        changePasswordBtn.setTextFill(ColorScheme.PRIMARY_600);
        changePasswordBtn.setPrefWidth(180);
        changePasswordBtn.setPrefHeight(45);
        changePasswordBtn.setStyle(
            "-fx-background-color: " + ColorScheme.PRIMARY_50_HEX + "; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;"
        );
        changePasswordBtn.setOnMouseEntered(e -> changePasswordBtn.setStyle(
            "-fx-background-color: " + ColorScheme.PRIMARY_100_HEX + "; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;"
        ));
        changePasswordBtn.setOnMouseExited(e -> changePasswordBtn.setStyle(
            "-fx-background-color: " + ColorScheme.PRIMARY_50_HEX + "; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;"
        ));
        changePasswordBtn.setOnAction(e -> showChangePasswordDialog());
        
        actionButtons.getChildren().addAll(editBtn, changePasswordBtn);

        card.getChildren().addAll(avatarContainer, infoSection, actionButtons);
        return card;
    }

    private void refreshProfileInfo() {
        infoSection.getChildren().clear();
        
        // Common fields for all users
        infoSection.getChildren().addAll(
            createInfoRow("👤", "Full Name", user.getFullName()),
            createInfoRow("🔑", "Username", user.getUsername()),
            createInfoRow("📧", "Email", user.getEmail()),
            createInfoRow("🏢", "Department", user.getDepartment() != null ? user.getDepartment() : "N/A"),
            createInfoRow("👔", "Role", getRoleDisplayName())
        );
        
        // Additional fields for students
        if (user.isStudent()) {
            try {
                StudentV2 student = studentDao.getStudentByStudentId(user.getUsername());
                if (student != null) {
                    infoSection.getChildren().addAll(
                        createInfoRow("🆔", "Student ID", student.getStudentId()),
                        createInfoRow("📚", "Year Level", "Year " + student.getYearLevel()),
                        createInfoRow("📊", "GPA", String.format("%.2f", student.getGpa()))
                    );
                }
            } catch (SQLException e) {
                System.err.println("Error loading student details: " + e.getMessage());
            }
        }
    }

    private StackPane createAvatar() {
        StackPane container = new StackPane();
        container.setPrefSize(120, 120);

        // Outer circle with gradient
        Circle outerCircle = new Circle(60);
        outerCircle.setFill(ColorScheme.PRIMARY_50);
        outerCircle.setStroke(ColorScheme.PRIMARY_200);
        outerCircle.setStrokeWidth(3);

        // Avatar icon
        Label avatarIcon = new Label(getAvatarEmoji());
        avatarIcon.setFont(FontLoader.getOutfitBold(48));

        container.getChildren().addAll(outerCircle, avatarIcon);
        return container;
    }

    private String getAvatarEmoji() {
        if (user.isAdmin()) {
            return "👨‍💼";
        } else if (user.isDepartmentHead()) {
            return "👨‍🏫";
        } else {
            return "👨‍🎓";
        }
    }

    private String getRoleDisplayName() {
        if (user.isAdmin()) {
            return "System Administrator";
        } else if (user.isDepartmentHead()) {
            return "Department Head";
        } else {
            return "Student";
        }
    }

    private HBox createInfoRow(String icon, String label, String value) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 20, 12, 20));
        row.setStyle(
            "-fx-background-color: " + ColorScheme.GRAY_50_HEX + "; " +
            "-fx-background-radius: 8;"
        );

        Label iconLabel = new Label(icon);
        iconLabel.setFont(FontLoader.getOutfitBold(20));
        iconLabel.setPrefWidth(30);
        
        VBox textBox = new VBox(2);
        HBox.setHgrow(textBox, Priority.ALWAYS);
        
        Label labelText = new Label(label);
        labelText.setFont(FontLoader.getOutfit(12));
        labelText.setTextFill(ColorScheme.MEDIUM_TEXT);

        Label valueText = new Label(value);
        valueText.setFont(FontLoader.getOutfitSemiBold(15));
        valueText.setTextFill(ColorScheme.DARK_TEXT);
        
        textBox.getChildren().addAll(labelText, valueText);

        row.getChildren().addAll(iconLabel, textBox);
        return row;
    }

    private void showEditProfileDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Profile");
        dialog.setHeaderText("Update your profile information");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        TextField emailField = new TextField(user.getEmail());
        emailField.setPromptText("Enter email address");

        grid.add(new Label("Email:"), 0, 0);
        grid.add(emailField, 1, 0);
        
        Label noteLabel = new Label("Note: Username, full name, and department cannot be changed.");
        noteLabel.setFont(FontLoader.getOutfit(12));
        noteLabel.setTextFill(ColorScheme.MEDIUM_TEXT);
        noteLabel.setWrapText(true);
        noteLabel.setMaxWidth(300);
        grid.add(noteLabel, 0, 1, 2, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String email = emailField.getText().trim();
                
                // Validate email
                ValidationResult emailResult = ValidationUtil.validateEmail(email);
                if (!emailResult.isValid()) {
                    showAlert("Validation Error", emailResult.getErrorMessage());
                    return;
                }
                
                try {
                    // Check if email is already used by another user
                    // TODO: Add method to check if email exists for different user
                    
                    // Update email in database
                    userDao.updateUserEmail(user.getId(), email);
                    
                    // Update local user object
                    user.setEmail(email);
                    
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Success");
                    success.setHeaderText("Profile Updated");
                    success.setContentText("Your email has been updated successfully!");
                    success.showAndWait();
                    
                    // Refresh the profile display
                    refreshProfileInfo();
                    
                } catch (SQLException e) {
                    showAlert("Database Error", "Failed to update profile: " + e.getMessage());
                }
            }
        });
    }
    
    private void showChangePasswordDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Update your password");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        PasswordField currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText("Enter current password");
        
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Enter new password");
        
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm new password");

        grid.add(new Label("Current Password:"), 0, 0);
        grid.add(currentPasswordField, 1, 0);
        grid.add(new Label("New Password:"), 0, 1);
        grid.add(newPasswordField, 1, 1);
        grid.add(new Label("Confirm Password:"), 0, 2);
        grid.add(confirmPasswordField, 1, 2);
        
        Label hintLabel = new Label("Password must be at least 6 characters long.");
        hintLabel.setFont(FontLoader.getOutfit(11));
        hintLabel.setTextFill(ColorScheme.MEDIUM_TEXT);
        grid.add(hintLabel, 0, 3, 2, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String currentPassword = currentPasswordField.getText();
                String newPassword = newPasswordField.getText();
                String confirmPassword = confirmPasswordField.getText();
                
                // Validate inputs
                if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    showAlert("Validation Error", "All fields are required.");
                    return;
                }
                
                if (newPassword.length() < 6) {
                    showAlert("Validation Error", "New password must be at least 6 characters long.");
                    return;
                }
                
                if (!newPassword.equals(confirmPassword)) {
                    showAlert("Validation Error", "New password and confirmation do not match.");
                    return;
                }
                
                try {
                    // Verify current password
                    User verifiedUser = userDao.login(user.getUsername(), currentPassword);
                    if (verifiedUser == null) {
                        showAlert("Authentication Error", "Current password is incorrect.");
                        return;
                    }
                    
                    // Update password
                    userDao.updatePassword(user.getId(), newPassword);
                    
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Success");
                    success.setHeaderText("Password Changed");
                    success.setContentText("Your password has been updated successfully!");
                    success.showAndWait();
                    
                } catch (SQLException e) {
                    showAlert("Database Error", "Failed to change password: " + e.getMessage());
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
