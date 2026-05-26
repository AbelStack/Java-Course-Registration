package com.university.crs.gui;

import com.university.crs.dao.StudentV2Dao;
import com.university.crs.dao.UserDao;
import com.university.crs.model.StudentV2;
import com.university.crs.model.User;
import com.university.crs.util.ValidationUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * Student Profile Page - View and edit profile information
 */
public class StudentProfilePage {

    private final Stage stage;
    private final User user;
    private final StudentV2Dao studentDao = new StudentV2Dao();
    private final UserDao userDao = new UserDao();
    
    private StudentV2 currentStudent;

    public StudentProfilePage(Stage stage, User user) {
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
            Label errorLabel = new Label("Failed to load student information");
            errorLabel.setFont(FontLoader.getOutfit(14));
            errorLabel.setTextFill(ColorScheme.ERROR_600);
            page.getChildren().add(errorLabel);
            return page;
        }

        // Header
        VBox header = new VBox(8);
        Label heading = new Label("My Profile");
        heading.setFont(FontLoader.getPoppinsBold(28));
        heading.setTextFill(ColorScheme.DARK_TEXT);
        
        Label subtitle = new Label("View and manage your account information");
        subtitle.setFont(FontLoader.getOutfit(14));
        subtitle.setTextFill(ColorScheme.MEDIUM_TEXT);
        
        header.getChildren().addAll(heading, subtitle);

        // Profile content
        HBox contentRow = new HBox(20);
        
        // Left column - Profile info
        VBox profileCard = buildProfileCard();
        
        // Right column - Actions
        VBox actionsCard = buildActionsCard();
        
        contentRow.getChildren().addAll(profileCard, actionsCard);
        HBox.setHgrow(profileCard, Priority.ALWAYS);

        page.getChildren().addAll(header, contentRow);
        
        ScrollPane scrollPane = new ScrollPane(page);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + ColorScheme.BACKGROUND_HEX + "; -fx-background-color: " + ColorScheme.BACKGROUND_HEX + ";");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        return scrollPane;
    }

    private VBox buildProfileCard() {
        VBox card = new VBox(24);
        card.setStyle(StyleConstants.card());
        card.setPadding(new Insets(30));
        
        // Profile header with avatar
        VBox profileHeader = new VBox(12);
        profileHeader.setAlignment(Pos.CENTER);
        
        Label avatar = new Label("👤");
        avatar.setFont(FontLoader.getOutfitBold(64));
        
        Label nameLabel = new Label(currentStudent.getName());
        nameLabel.setFont(FontLoader.getPoppinsBold(22));
        nameLabel.setTextFill(ColorScheme.DARK_TEXT);
        
        Label studentIdLabel = new Label(currentStudent.getStudentId());
        studentIdLabel.setFont(FontLoader.getOutfitSemiBold(14));
        studentIdLabel.setTextFill(ColorScheme.PRIMARY_600);
        
        profileHeader.getChildren().addAll(avatar, nameLabel, studentIdLabel);
        
        // Divider
        Separator divider = new Separator();
        divider.setPadding(new Insets(10, 0, 10, 0));
        
        // Profile details
        VBox detailsBox = new VBox(16);
        
        detailsBox.getChildren().addAll(
            createProfileField("📧", "Email", currentStudent.getEmail()),
            createProfileField("🏢", "Department", currentStudent.getDepartmentName()),
            createProfileField("📚", "Year Level", "Year " + currentStudent.getYearLevel()),
            createProfileField("📊", "GPA", String.format("%.2f", currentStudent.getGpa())),
            createProfileField("🔑", "Student ID", currentStudent.getStudentId())
        );
        
        card.getChildren().addAll(profileHeader, divider, detailsBox);
        
        return card;
    }

    private HBox createProfileField(String icon, String label, String value) {
        HBox field = new HBox(15);
        field.setAlignment(Pos.CENTER_LEFT);
        field.setPadding(new Insets(8));
        field.setStyle(
            "-fx-background-color: " + ColorScheme.GRAY_50_HEX + "; " +
            "-fx-background-radius: 8;"
        );
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(FontLoader.getOutfitBold(18));
        iconLabel.setPrefWidth(30);
        
        VBox textBox = new VBox(2);
        HBox.setHgrow(textBox, Priority.ALWAYS);
        
        Label labelText = new Label(label);
        labelText.setFont(FontLoader.getOutfit(12));
        labelText.setTextFill(ColorScheme.MEDIUM_TEXT);
        
        Label valueText = new Label(value);
        valueText.setFont(FontLoader.getOutfitSemiBold(14));
        valueText.setTextFill(ColorScheme.DARK_TEXT);
        
        textBox.getChildren().addAll(labelText, valueText);
        
        field.getChildren().addAll(iconLabel, textBox);
        
        return field;
    }

    private VBox buildActionsCard() {
        VBox card = new VBox(20);
        card.setStyle(StyleConstants.card());
        card.setPadding(new Insets(30));
        card.setPrefWidth(350);
        
        Label title = new Label("Account Actions");
        title.setFont(FontLoader.getOutfitSemiBold(18));
        title.setTextFill(ColorScheme.DARK_TEXT);
        
        // Change Password button
        Button changePasswordBtn = new Button("🔒 Change Password");
        changePasswordBtn.setFont(FontLoader.getOutfitSemiBold(14));
        changePasswordBtn.setTextFill(ColorScheme.DARK_TEXT);
        changePasswordBtn.setPrefHeight(45);
        changePasswordBtn.setMaxWidth(Double.MAX_VALUE);
        changePasswordBtn.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: " + ColorScheme.GRAY_300_HEX + "; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;"
        );
        changePasswordBtn.setOnMouseEntered(e -> changePasswordBtn.setStyle(
            "-fx-background-color: " + ColorScheme.GRAY_50_HEX + "; " +
            "-fx-border-color: " + ColorScheme.PRIMARY_500_HEX + "; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;"
        ));
        changePasswordBtn.setOnMouseExited(e -> changePasswordBtn.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: " + ColorScheme.GRAY_300_HEX + "; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;"
        ));
        changePasswordBtn.setOnAction(e -> showChangePasswordDialog());
        
        // Edit Profile button
        Button editProfileBtn = new Button("✏️ Edit Profile");
        editProfileBtn.setFont(FontLoader.getOutfitSemiBold(14));
        editProfileBtn.setTextFill(ColorScheme.DARK_TEXT);
        editProfileBtn.setPrefHeight(45);
        editProfileBtn.setMaxWidth(Double.MAX_VALUE);
        editProfileBtn.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: " + ColorScheme.GRAY_300_HEX + "; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;"
        );
        editProfileBtn.setOnMouseEntered(e -> editProfileBtn.setStyle(
            "-fx-background-color: " + ColorScheme.GRAY_50_HEX + "; " +
            "-fx-border-color: " + ColorScheme.PRIMARY_500_HEX + "; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;"
        ));
        editProfileBtn.setOnMouseExited(e -> editProfileBtn.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: " + ColorScheme.GRAY_300_HEX + "; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;"
        ));
        editProfileBtn.setOnAction(e -> showEditProfileDialog());
        
        card.getChildren().addAll(title, changePasswordBtn, editProfileBtn);
        
        return card;
    }

    private void showChangePasswordDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Enter your new password");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        PasswordField currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText("Current password");
        
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New password");
        
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm new password");

        grid.add(new Label("Current Password:"), 0, 0);
        grid.add(currentPasswordField, 1, 0);
        grid.add(new Label("New Password:"), 0, 1);
        grid.add(newPasswordField, 1, 1);
        grid.add(new Label("Confirm Password:"), 0, 2);
        grid.add(confirmPasswordField, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String currentPassword = currentPasswordField.getText();
                String newPassword = newPasswordField.getText();
                String confirmPassword = confirmPasswordField.getText();
                
                // Validate
                if (!newPassword.equals(confirmPassword)) {
                    showAlert("Error", "New passwords do not match!");
                    return;
                }
                
                var passwordResult = ValidationUtil.validatePassword(newPassword);
                if (!passwordResult.isValid()) {
                    showAlert("Validation Error", passwordResult.getErrorMessage());
                    return;
                }
                
                try {
                    // TODO: Verify current password and update
                    userDao.updatePassword(user.getId(), newPassword);
                    
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Success");
                    success.setHeaderText(null);
                    success.setContentText("Password changed successfully!");
                    success.showAndWait();
                } catch (SQLException e) {
                    showAlert("Error", "Failed to change password: " + e.getMessage());
                }
            }
        });
    }

    private void showEditProfileDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Profile");
        dialog.setHeaderText("Update your profile information");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField(currentStudent.getName());
        TextField emailField = new TextField(currentStudent.getEmail());

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                var nameResult = ValidationUtil.validateName(nameField.getText());
                if (!nameResult.isValid()) {
                    showAlert("Validation Error", nameResult.getErrorMessage());
                    return;
                }
                
                var emailResult = ValidationUtil.validateEmail(emailField.getText());
                if (!emailResult.isValid()) {
                    showAlert("Validation Error", emailResult.getErrorMessage());
                    return;
                }
                
                try {
                    studentDao.updateStudent(currentStudent.getId(), nameResult.getStringValue(), 
                        emailResult.getStringValue(), currentStudent.getDepartmentId(), currentStudent.getYearLevel());
                    
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Success");
                    success.setHeaderText(null);
                    success.setContentText("Profile updated successfully!");
                    success.showAndWait();
                    
                    // Reload student data
                    currentStudent = studentDao.getStudentById(currentStudent.getId());
                    
                } catch (SQLException e) {
                    showAlert("Error", "Failed to update profile: " + e.getMessage());
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
